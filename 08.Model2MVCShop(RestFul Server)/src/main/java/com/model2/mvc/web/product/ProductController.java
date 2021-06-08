package com.model2.mvc.web.product;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;


//==> 상품관리 Controller
@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	///Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	//setter Method 구현 않음
		
	public ProductController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml 참조 할것
	//==> 아래의 두개를 주석을 풀어 의미를 확인 할것
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	
	@RequestMapping(value="/addProduct", method=RequestMethod.POST)
	public String addProduct( @ModelAttribute("product") Product product, Model model ) throws Exception {

		System.out.println("/addProduct : POST");
		String tempManuDate = product.getManuDate().replaceAll("-", "");
		product.setManuDate(tempManuDate);
		
		//Business Logic
		productService.addProduct(product);
		model.addAttribute(product);
		
		return "forward:/product/addProduct.jsp";
	}
	
	@RequestMapping(value="/getProduct")
	public String getProduct( @RequestParam("prodNo") int prodNo , Model model, HttpServletResponse response ) throws Exception {
		
		System.out.println("/getProduct");
		//Business Logic
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		model.addAttribute("product", product);
		
		Cookie cookie = new Cookie("history"+prodNo, URLEncoder.encode(product.getProdName()));
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
		System.out.println("history=prodNo 쿠키 저장완료 : "+cookie);	
				
		return "forward:/product/getProduct.jsp";
	}
	
	@RequestMapping(value="/updateProduct/{prodNo}/{currentPage}", method=RequestMethod.GET)
	public String updateProduct( @PathVariable int prodNo , @PathVariable int currentPage, Model model ) throws Exception{

		System.out.println("/updateProduct : GET");
		//Business Logic
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		model.addAttribute("product", product);
		model.addAttribute("currentPage", currentPage);
		
		return "forward:/product/updateProductView.jsp";
	}
	
	@RequestMapping(value="/updateProduct", method=RequestMethod.POST)
	public String updateProduct( @ModelAttribute("product") Product product , Model model ) throws Exception{

		System.out.println("/updateProduct : POST");
		//Business Logic
		productService.updateProduct(product);
		
		return "forward:/product/getProduct";
	}
	
	
	@RequestMapping("/listProduct")
	public String listProduct( @ModelAttribute("search") Search search , Model model , HttpServletRequest request) throws Exception{
		
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
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/product/listProduct.jsp";
	}
	
	@RequestMapping("/deleteProduct/{prodNo}")
	public String deleteProduct(@PathVariable int prodNo , Model model) throws Exception{
		
		System.out.println("/deleteProduct 시작 / prodNo : "+prodNo);
		
		//DB에서 상품정보 삭제
		productService.deleteProduct(prodNo);
		
		System.out.println("/deleteProduct 완료");
		
		return "redirect:/purchase/listSale?menu=manage";
	}
	
	
}