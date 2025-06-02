package com.team.teamreadioserver.user.admin.service;

import com.team.teamreadioserver.user.admin.dto.AdminUserListResponse;
import com.team.teamreadioserver.user.admin.dto.AdminUserSearchDTO;
import com.team.teamreadioserver.user.admin.dto.AdminUserViewDTO;
import com.team.teamreadioserver.user.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

  private final AdminMapper adminMapper;

//  public List<AdminUserViewDTO> getAdminUserList() {
//    return adminMapper.selectAdminUserList();
//  }

  public List<AdminUserViewDTO> getAdminUserList(AdminUserSearchDTO searchDTO) {
    return adminMapper.selectAdminUserList(searchDTO);
  }

  public AdminUserListResponse getPagedAdminUserList(AdminUserSearchDTO searchDTO) {
    int totalCount = getAdminUserCount(searchDTO);

    int size = searchDTO.getSize();
    int totalPages = (totalCount + size - 1) / size;

    searchDTO.calculateOffset();

    List<AdminUserViewDTO> userList = getAdminUserList(searchDTO);

    AdminUserListResponse response = new AdminUserListResponse();
    response.setUsers(userList);
    response.setTotalCount(totalCount);
    response.setTotalPages(totalPages);

    return response;
  }

  public int getAdminUserCount(AdminUserSearchDTO searchDTO) {
    return adminMapper.countAdminUserList(searchDTO); // Mapper에 count 쿼리 추가 필요
  }

  @Transactional
  public void changeUserRole(String userId, String newRole) {
    adminMapper.updateUserRole(userId, newRole);
  }

  public void deleteUser(String userId) {
    adminMapper.deleteUser(userId);
  }


}
