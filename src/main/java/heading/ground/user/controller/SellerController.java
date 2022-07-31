package heading.ground.user.controller;

import heading.ground.common.Paging;
import heading.ground.booking.dto.postDtos.MenuDto;
import heading.ground.user.dto.SellerDto;
import heading.ground.booking.entity.post.Menu;
import heading.ground.user.entity.Seller;
import heading.ground.user.repository.UserRepository;
import heading.ground.booking.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequiredArgsConstructor
public class SellerController {

    private final PostService postService;
    private final UserRepository userRepository;

    @GetMapping("/seller/{id}/menus") //해당 가게의 전체 메뉴
    public String menus(@PathVariable("id") Long id,
                        Pageable pageable,
                        Model model){
        int page = (pageable.getPageNumber()==0)?0:(pageable.getPageNumber()-1);
        Page<MenuDto> all = postService.pageBySeller(page, 9,id);
        Paging paging = postService.pageTemp(all,null);
        Optional<MenuDto> first = all.get().findFirst();
        if(first.isPresent())
            model.addAttribute("seller_name",first.get().getSeller());

        model.addAttribute("sid",id);
        model.addAttribute("menus",all);
        model.addAttribute("paging",paging);

        return "post/menusBySeller";
    }

    //쿼리DSL 적용
    @GetMapping("/sellerInfo/{id}")
    public String sellerInfo(@PathVariable("id") Long id, Model model) {
        Seller seller = userRepository.findByIdWithMenu(id);

        SellerDto sellerDto = SellerDto.builder()
                .id(seller.getId())
                .doro(seller.getAddress().getDoro())
                .doro_spec(seller.getAddress().getDoro_spec())
                .name(seller.getName())
                .phoneNumber(seller.getPhoneNumber())
                .desc(seller.getDesc())
                .photo((seller.getImageFile()!=null)?seller.getImageFile().getStoreName():null)
                .build();

        //대표메뉴 3가지
        Set<Menu> menus = seller.getMenus();
        List<Menu> collect = menus.stream().filter(m -> m.isBest()).collect(Collectors.toList());
        List<MenuDto> menuDtos = collect.stream().map(m -> new MenuDto(m)).collect(Collectors.toList());

        model.addAttribute("seller", sellerDto);
        model.addAttribute("best", menuDtos);

        return "user/seller";
    }
}
