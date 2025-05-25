//package com.team.teamreadioserver.user.typehandler;
//
//import com.team.teamreadioserver.user.entity.UserRole;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {
//
//  @Override
//  public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType) throws SQLException {
//    ps.setString(i, parameter.name());  // Enum → String
//  }
//
//  @Override
//  public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
//    String role = rs.getString(columnName);
//    return role == null ? null : UserRole.valueOf(role);  // String → Enum
//  }
//
//  @Override
//  public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//    String role = rs.getString(columnIndex);
//    return role == null ? null : UserRole.valueOf(role);
//  }
//
//  @Override
//  public UserRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//    String role = cs.getString(columnIndex);
//    return role == null ? null : UserRole.valueOf(role);
//  }
//}
