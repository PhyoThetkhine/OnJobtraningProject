package com.prj.LoneHPManagement.Service;

import com.prj.LoneHPManagement.model.entity.DealerProduct;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface DealerProductService {
    DealerProduct updateProductStatus(int id, String status);
    Page<DealerProduct> getAllDealerProducts(int page, int size, String sortBy);
    List<DealerProduct> getAllDealerProducts();
    List<DealerProduct> getProductsForSelect(String searchTerm);
    Optional<DealerProduct> getDealerProductById(int id);

//    List<DealerProduct> getDealerProductsByCompanyId(int companyId);

    DealerProduct createDealerProduct(int cifId, DealerProduct dealerProduct);

    DealerProduct updateDealerProduct(int id, DealerProduct dealerProductDetails);

    void deleteDealerProduct(int id);


}
