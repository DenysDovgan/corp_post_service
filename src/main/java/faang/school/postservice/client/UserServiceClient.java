package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    //тестовый метод, его нет в сервисе user-service, но думаю что должно быть как то так
    @GetMapping("/v1/users/{userId}/subscribers")
    List<Long> getUserSubscribers(@PathVariable long userId);
}
