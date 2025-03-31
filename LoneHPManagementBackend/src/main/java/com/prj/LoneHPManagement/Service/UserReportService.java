package com.prj.LoneHPManagement.Service;
import com.prj.LoneHPManagement.model.dto.UserDTO;
import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface UserReportService {
    ByteArrayOutputStream generateAllUsersPdfReport() throws JRException;
    ByteArrayOutputStream generateUsersPdfReportByStatus(String status) throws JRException;
    ByteArrayOutputStream generateAllUsersExcelReport() throws JRException;
    ByteArrayOutputStream generateUsersExcelReportByStatus(String status) throws JRException;
    ByteArrayOutputStream generateUserPdfReport(int branchId) throws JRException;
    ByteArrayOutputStream generateUserExcelReport(int branchId) throws JRException;
    List<UserDTO> getAllUserDTOs(int branchId);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUserDTOsByStatus(String status);
}
