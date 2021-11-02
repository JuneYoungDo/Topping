package com.teenteen.topping.category.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class MainCategoryRes {
    private Long categoryId;
    private String name;
    private boolean picked;    // 선택된 것인지
}
