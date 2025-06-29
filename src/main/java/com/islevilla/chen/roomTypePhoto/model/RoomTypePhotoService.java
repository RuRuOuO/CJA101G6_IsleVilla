package com.islevilla.chen.roomTypePhoto.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.islevilla.chen.roomType.model.RoomType;
import com.islevilla.chen.roomType.model.RoomTypeRepository;
import com.islevilla.chen.util.exception.BusinessException;

@Service
public class RoomTypePhotoService {
	@Autowired
	private RoomTypePhotoRepository roomTypePhotoRepository;
	@Autowired
	private RoomTypeRepository roomTypeRepository;

	
	@Transactional
	public RoomTypePhoto addRoomTypePhoto(Integer roomTypeId, Byte[] roomTypePhoto, Integer displayOrder) {
		// 驗證輸入的房型ID是否為空值
		if (roomTypeId == null) {
			throw new BusinessException("房型編號不能為空");
		}
		
	    // 主鍵不用設，JPA 會自動產生
	    // 設定關聯（需查詢 RoomType 物件再設進去）
	    RoomType roomType = roomTypeRepository.findById(roomTypeId)
	    	.orElseThrow(() -> new BusinessException("房型不存在，請重新選擇"));

	    //圖片為空值
	    if(roomTypePhoto == null || roomTypePhoto.length == 0) {
	    	throw new BusinessException("未上傳圖片");
	    }

	    // 沒有指定顯示順序，自動設為最大值+1
        if (displayOrder == null) {
            Integer maxOrder = roomTypePhotoRepository.findMaxDisplayOrderByRoomTypeId(roomTypeId);
            displayOrder = maxOrder + 1;
        }
        
        RoomTypePhoto roomTypephoto = new RoomTypePhoto();
	    roomTypephoto.setRoomType(roomType);
	    roomTypephoto.setRoomTypePhoto(roomTypePhoto);
	    roomTypephoto.setDisplayOrder(displayOrder);
	    
        return roomTypePhotoRepository.save(roomTypephoto);
	}
	
	@Transactional
	public RoomTypePhoto updateRoomTypePhoto(RoomTypePhoto roomTypePhoto) {
		// 驗證是否存在
        if (roomTypePhoto.getRoomTypePhotoId() != null) {
            if (!roomTypePhotoRepository.existsById(roomTypePhoto.getRoomTypePhotoId())) {
                throw new BusinessException("找不到照片 ID: " + roomTypePhoto.getRoomTypePhotoId());
            }
        }
        
        // 驗證輸入的房型ID是否為空值
 		if (roomTypePhoto.getRoomType().getRoomTypeId() == null) {
 			throw new BusinessException("房型編號不能為空");
 		}
 		
 		// 主鍵不用設，JPA 會自動產生
 		// 設定關聯（需查詢 RoomType 物件再設進去）
 		roomTypeRepository.findById(roomTypePhoto.getRoomType().getRoomTypeId())
 				.orElseThrow(() -> new BusinessException("房型不存在，請重新選擇"));
 		
 		// 更新時如果沒有新圖片，保持原有圖片（不檢查圖片為空）
	    // 只有在確實有新圖片傳入時才檢查
	    // 這個檢查應該在Controller層處理
        
        return roomTypePhotoRepository.save(roomTypePhoto);
    }
	
	//更新照片顯示順序
    @Transactional
    public RoomTypePhoto updateDisplayOrder(Integer roomTypePhotoId, Integer newDisplayOrder) {
        RoomTypePhoto photo = findById(roomTypePhotoId);
        photo.setDisplayOrder(newDisplayOrder);
        return roomTypePhotoRepository.save(photo);
    }
	
	@Transactional
    public void deleteRoomTypePhoto(Integer roomTypePhotoId) {
        if (!roomTypePhotoRepository.existsById(roomTypePhotoId)) {
            throw new BusinessException("找不到照片 ID: " + roomTypePhotoId);
        }
        roomTypePhotoRepository.deleteById(roomTypePhotoId);
    }
	
	@Transactional(readOnly = true)
	public RoomTypePhoto findById(Integer roomTypePhotoId) {
		Optional<RoomTypePhoto> optional=roomTypePhotoRepository.findById(roomTypePhotoId);
		return optional.orElse(null);
	}
	
	@Transactional(readOnly = true)
	public List<RoomTypePhoto> findAll() {
		return roomTypePhotoRepository.findAll();
	}
	
	//根據房型ID查詢照片（按顯示順序）
	@Transactional(readOnly = true)
	public List<RoomTypePhoto> roomTypeFindPhotos(Integer roomTypeId){
		return roomTypePhotoRepository.findWithPhotos(roomTypeId);
	}
	 
	//複合查詢
	@Transactional(readOnly = true)
	public List<RoomTypePhoto> compoundQuery(Integer roomTypePhotoId, Integer roomTypeId){
	    List<RoomTypePhoto> result = roomTypePhotoRepository.search(roomTypePhotoId, roomTypeId);
	    if (result.isEmpty()) {
	        throw new BusinessException("查無符合條件的房間資料！");
	    }
	    return result;
	}
}
