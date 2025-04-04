package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.UserReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
public class UserReportController {

    @Autowired
    private UserReportService userReportService;

    @GetMapping("/users/pdf/branch/{branchId}")
    public ResponseEntity<Resource> getUserPdfReportByBranch(
            @PathVariable int branchId) throws JRException {
        try {
            ByteArrayOutputStream reportStream = userReportService.generateUserPdfReport(branchId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);
        ByteArrayResource resource = new ByteArrayResource(reportStream.toByteArray());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(reportStream.size())
                .body(resource);
            }catch (JRException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/excel/branch/{branchId}")
    public ResponseEntity<Resource> getUserExcelReportByBranch(
            @PathVariable int branchId) throws JRException {
        ByteArrayOutputStream reportStream = userReportService.generateUserExcelReport(branchId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_report.xls");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ByteArrayResource resource = new ByteArrayResource(reportStream.toByteArray());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(reportStream.size())
                .body(resource);
    }
    @GetMapping("/users/pdf")
    public ResponseEntity<Resource> getUserPdfReport(
            @RequestParam(value = "status", required = false) String status) throws JRException {
        System.out.println("Generating PDF report for status: " + (status != null ? status : "all"));

        ByteArrayOutputStream reportStream = status != null
                ? userReportService.generateUsersPdfReportByStatus(status)
                : userReportService.generateAllUsersPdfReport();

        HttpHeaders headers = new HttpHeaders();
        String filename = status != null ? "user_report_" + status.toLowerCase() + ".pdf" : "user_report.pdf";
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentType(MediaType.APPLICATION_PDF);

        ByteArrayResource resource = new ByteArrayResource(reportStream.toByteArray());
        System.out.println("PDF report size: " + reportStream.size() + " bytes");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(reportStream.size())
                .body(resource);
    }

    @GetMapping("/users/excel")
    public ResponseEntity<Resource> getUserExcelReport(
            @RequestParam(value = "status", required = false) String status) throws JRException {
        System.out.println("Generating Excel report for status: " + (status != null ? status : "all"));

        ByteArrayOutputStream reportStream = status != null
                ? userReportService.generateUsersExcelReportByStatus(status)
                : userReportService.generateAllUsersExcelReport();

        HttpHeaders headers = new HttpHeaders();
        String filename = status != null ? "user_report_" + status.toLowerCase() + ".xlsx" : "user_report.xlsx";
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        ByteArrayResource resource = new ByteArrayResource(reportStream.toByteArray());
        System.out.println("Excel report size: " + reportStream.size() + " bytes");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(reportStream.size())
                .body(resource);
    }
}
