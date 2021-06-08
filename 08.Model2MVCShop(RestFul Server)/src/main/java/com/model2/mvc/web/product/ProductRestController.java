package com.model2.mvc.web.product;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

@RestController
@RequestMapping("/product/*")
public class ProductRestController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	public ProductRestController() {
		System.out.println(this.getClass());
	}
	
	
	@RequestMapping(value="/json/addProduct", method=RequestMethod.POST)
	public Product addProduct( @RequestBody Product product ) throws Exception {

		System.out.println("/json/addProduct : POST");
		
		if(product.getManuDate()!=null) {
			String tempManuDate = product.getManuDate().replaceAll("-", "");
			product.setManuDate(tempManuDate);
		}
		
		//Business Logic
		productService.addProduct(product);
		System.out.println("/json/addProduct : POST 완료");
		
		return product;
	}
	
	@RequestMapping(value="/json/getProduct/{prodNo}", method=RequestMethod.GET)
	public Product getProduct( @PathVariable int prodNo , HttpServletResponse response ) throws Exception {
		
		System.out.println("/json/getProduct/{prodNo} 시작");
		//Business Logic
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		
		Cookie cookie = new Cookie("history"+prodNo, URLEncoder.encode(product.getProdName()));
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
		System.out.println("history=prodNo 쿠키 저장완료 : "+cookie);	
				
		return product;
	}
	
	/*
	@RequestMapping("/json/listProduct")
	public Map listProduct( @RequestBody Search search , Model model , HttpServletRequest request) throws Exception{
		
		System.out.println("/listProduct");		
		
		System.out.println("search 검색어 : "+search.getSearchKeyword());
		System.out.println("search 가격 : "+search.getSearchPriceMin()+" ~ "+search.getSearchPriceMax());
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic 수행
		Map<String , Object> map=productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		map.put("resultPage", resultPage);
		map.put("search", search);
		
		return map;
	}
	*/

}
