//package com.teenteen.topping.fcm.VO;
//
//import com.teenteen.topping.user.VO.User;
//import lombok.*;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//@Entity(name = "message")
//@Builder
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor
//public class SystemMessage implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long messageId;
//    private String title;
//    private String body;
//    private boolean deleted;
//    private LocalDateTime createdAt;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User sendUser;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User receiveUser;
//}
