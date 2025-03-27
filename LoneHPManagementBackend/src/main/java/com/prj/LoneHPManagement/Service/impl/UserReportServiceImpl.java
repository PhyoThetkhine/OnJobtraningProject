package com.prj.LoneHPManagement.Service.impl;


import com.prj.LoneHPManagement.model.dto.UserDTO;
import com.prj.LoneHPManagement.model.entity.User;
import com.prj.LoneHPManagement.model.repo.UserRepository;
import com.prj.LoneHPManagement.Service.UserReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserReportServiceImpl implements UserReportService {

    private static final Logger logger = LoggerFactory.getLogger(UserReportServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public ByteArrayOutputStream generateUserPdfReport(int branchId) throws JRException {
        JasperPrint jasperPrint = prepareJasperPrint(branchId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        logger.info("Generated PDF report successfully for branchId: {}", branchId);
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream generateUserExcelReport(int branchId) throws JRException {
        JasperPrint jasperPrint = prepareJasperPrint(branchId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setDetectCellType(true);
        configuration.setWhitePageBackground(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        logger.info("Generated Excel report successfully for branchId: {}", branchId);
        return outputStream;
    }
    @Override
    public ByteArrayOutputStream generateAllUsersPdfReport() throws JRException {
        JasperPrint jasperPrint = prepareJasperPrintForAllUsers();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        logger.info("Generated PDF report successfully for all users");
        return outputStream;
    }

    @Override
    public ByteArrayOutputStream generateAllUsersExcelReport() throws JRException {
        JasperPrint jasperPrint = prepareJasperPrintForAllUsers();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setDetectCellType(true);
        configuration.setWhitePageBackground(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        logger.info("Generated Excel report successfully for all users");
        return outputStream;
    }


    @Override
    public List<UserDTO> getAllUserDTOs(int branchId) {


         List<User>  users = userRepository.findByBranch_Id(branchId);
            logger.info("Fetched users for branchId: {}", branchId);


        return users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUserCode(user.getUserCode());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setRoleName(user.getRole().getRoleName());
            dto.setBranchName(user.getBranch().getBranchName());
            dto.setStatus(user.getStatusDescription());
            dto.setCreatedDate(user.getCreatedDate());
            return dto;
        }).collect(Collectors.toList());
    }
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll(); // Assuming findAll() is available
        logger.info("Fetched all users successfully");

        return users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUserCode(user.getUserCode());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setRoleName(user.getRole().getRoleName());
            dto.setBranchName(user.getBranch().getBranchName());
            dto.setStatus(user.getStatusDescription());
            dto.setCreatedDate(user.getCreatedDate());
            return dto;
        }).collect(Collectors.toList());
    }


    private JasperPrint prepareJasperPrint(int branchId) throws JRException {
        List<UserDTO> userDTOs = getAllUserDTOs(branchId);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userDTOs);

        JasperReport jasperReport;
        ClassPathResource jasperResource = new ClassPathResource("reports/user_report.jasper");
        ClassPathResource jrxmlResource = new ClassPathResource("reports/user_report.jrxml");

        try {
            logger.info("Checking for user_report.jasper: exists=" + jasperResource.exists());
            if (jasperResource.exists()) {
                jasperReport = (JasperReport) JRLoader.loadObject(jasperResource.getInputStream());
                logger.info("Loaded user_report.jasper successfully");
            } else {
                logger.info("Checking for user_report.jrxml: exists=" + jrxmlResource.exists());
                if (jrxmlResource.exists()) {
                    jasperReport = JasperCompileManager.compileReport(jrxmlResource.getInputStream());
                    logger.info("Compiled user_report.jrxml successfully");
                } else {
                    throw new JRException("Neither user_report.jasper nor user_report.jrxml found in reports/");
                }
            }
        } catch (IOException e) {
            logger.error("Error loading or compiling report", e);
            throw new JRException("Error loading or compiling report template", e);
        }

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("REPORT_TITLE", "Staff Report - LoneHP Management (All Users By Branch )");

        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }
    private JasperPrint prepareJasperPrintForAllUsers() throws JRException {
        List<UserDTO> userDTOs = getAllUsers();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userDTOs);

        JasperReport jasperReport;
        ClassPathResource jasperResource = new ClassPathResource("reports/user_report.jasper");
        ClassPathResource jrxmlResource = new ClassPathResource("reports/user_report.jrxml");

        try {
            logger.info("Checking for user_report.jasper: exists=" + jasperResource.exists());
            if (jasperResource.exists()) {
                jasperReport = (JasperReport) JRLoader.loadObject(jasperResource.getInputStream());
                logger.info("Loaded user_report.jasper successfully");
            } else {
                logger.info("Checking for user_report.jrxml: exists=" + jrxmlResource.exists());
                if (jrxmlResource.exists()) {
                    jasperReport = JasperCompileManager.compileReport(jrxmlResource.getInputStream());
                    logger.info("Compiled user_report.jrxml successfully");
                } else {
                    throw new JRException("Neither user_report.jasper nor user_report.jrxml found in reports/");
                }
            }
        } catch (IOException e) {
            logger.error("Error loading or compiling report", e);
            throw new JRException("Error loading or compiling report template", e);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "Staff Report - LoneHP Management (All Users)");

        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

}