package com.teenteen.topping.category.CategoryDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MainCategoryReq {
    private Long pick1;
    private Long pick2;
    private Long pick3;
    private Long pick4;
    private Long pick5;
    private Long pick6;

    public List<Long> makePicks() {
        if (getPick4() == null)
            return List.of(this.pick1, this.pick2, this.pick3);
        else if (getPick5() == null)
            return List.of(this.pick1, this.pick2, this.pick3, this.pick4);
        else if (getPick6() == null)
            return List.of(this.pick1, this.pick2, this.pick3, this.pick4, this.pick5);
        else
            return List.of(this.pick1, this.pick2, this.pick3, this.pick4, this.pick5, this.pick6);
    }

    ;
}
