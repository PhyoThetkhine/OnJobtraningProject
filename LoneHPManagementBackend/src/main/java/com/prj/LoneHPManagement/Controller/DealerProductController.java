package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.Service.DealerProductService;
import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.dto.PagedResponse;
import com.prj.LoneHPManagement.model.entity.DealerProduct;
import com.prj.LoneHPManagement.model.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dealer-products")
public class DealerProductController {

    @Autowired
    private DealerProductService dealerProductService;
    // DealerProductController.java
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<PagedResponse<DealerProduct>>> getAllDealerProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<DealerProduct> products = dealerProductService.getAllDealerProducts(page, size, sortBy);

        PagedResponse<DealerProduct> pagedResponse = new PagedResponse<>(
                products.getContent(),
                products.getTotalPages(),
                products.getTotalElements(),
                products.getSize(),
                products.getNumber(),
                products.getNumberOfElements(),
                products.isFirst(),
                products.isLast(),
                products.isEmpty()
        );

        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Dealer products retrieved successfully",
                pagedResponse
        ));
    }
    @GetMapping("/getForSelect")
    public ResponseEntity<List<DealerProduct>> getForSelect(@RequestParam(defaultValue = "") String searchTerm) {
        List<DealerProduct> products = dealerProductService.getProductsForSelect(searchTerm);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<?> getDealerProductById(@PathVariable int id) {
        Optional<DealerProduct> dealerProduct = dealerProductService.getDealerProductById(id);
        return dealerProduct
                .map(product -> ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Dealer product fetched successfully", product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.success(HttpStatus.NOT_FOUND.value(), "Dealer product not found", null)));
    }

    @PostMapping("/save/{companyId}")
    public ResponseEntity<?> createDealerProduct(@PathVariable int companyId, @RequestBody DealerProduct dealerProduct) {
        DealerProduct savedDealerProduct = dealerProductService.createDealerProduct(companyId, dealerProduct);
        return new ResponseEntity<>(ApiResponse.success(HttpStatus.CREATED.value(), "Dealer product created successfully", savedDealerProduct), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDealerProduct(@PathVariable int id, @RequestBody DealerProduct dealerProductDetails) {
        try {
            DealerProduct updatedDealerProduct = dealerProductService.updateDealerProduct(id, dealerProductDetails);
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Dealer product updated successfully", updatedDealerProduct));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.success(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDealerProduct(@PathVariable int id) {
        dealerProductService.deleteDealerProduct(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Dealer product deleted successfully", null));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getDealerProductsByCompanyId(@PathVariable int companyId) {
        List<DealerProduct> dealerProducts = dealerProductService.getDealerProductsByCompanyId(companyId);

        if (dealerProducts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No dealer products found for this company"));
        }

        ApiResponse<List<DealerProduct>> response = ApiResponse.success(HttpStatus.OK.value(), "Dealer products fetched successfully", dealerProducts);
        return ResponseEntity.ok(response);
    }

}
