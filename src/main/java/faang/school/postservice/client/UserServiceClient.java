package faang.school.postservice.client;

import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.model.dto.UserWithFollowersDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "user-service",
        url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/maxId")
    Long getMaxUserId();

    @GetMapping("/users/{userId}/with-followers")
    UserWithFollowersDto getUserWithFollowers(@PathVariable long userId);
}
