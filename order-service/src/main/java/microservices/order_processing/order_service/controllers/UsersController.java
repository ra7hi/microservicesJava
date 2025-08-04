package microservices.order_processing.order_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.services.UsersServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API для управления пользователями")
public class UsersController {

    private final UsersServiceImp usersServiceImp;

    @Operation(
            summary = "Получить информацию о пользователе по имени",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден",
                            content = @Content(schema = @Schema(implementation = Users.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = microservices.order_processing.order_service.controllers.responses.ApiResponse.class)))
            }
    )
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserInfo(
            @Parameter(description = "Имя пользователя для поиска", example = "john_doe")
            @PathVariable String username) {
        Optional<Users> user = usersServiceImp.findUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new microservices.order_processing.order_service.controllers.responses.ApiResponse(false, "User not found!"));
        }
    }

    @Operation(
            summary = "Создать нового пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для создания",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно создан",
                            content = @Content(schema = @Schema(implementation = microservices.order_processing.order_service.controllers.responses.ApiResponse.class)))
            }
    )
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @Validated @RequestBody UserDto userDto) {
        usersServiceImp.createUser(userDto);
        return ResponseEntity.ok(new microservices.order_processing.order_service.controllers.responses.ApiResponse(true, "User created successfully!"));
    }

    @Operation(
            summary = "Обновить информацию пользователя",
            parameters = @Parameter(description = "Имя пользователя для обновления", example = "john_doe"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новые данные пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @PatchMapping("/update/{username}")
    public ResponseEntity<?> updateUserInfo(
            @Parameter(description = "Имя пользователя для обновления", example = "john_doe")
            @PathVariable String username,
            @Validated @RequestBody UserDto userDto) {
        usersServiceImp.updateUser(username, userDto);
        return ResponseEntity.ok(new microservices.order_processing.order_service.controllers.responses.ApiResponse(true, "User " + username + " has been updated successfully!"));
    }

    @Operation(
            summary = "Удалить пользователя по имени",
            parameters = @Parameter(description = "Имя пользователя для удаления", example = "john_doe"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно удален",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> removeUser(
            @Parameter(description = "Имя пользователя для удаления", example = "john_doe")
            @PathVariable String username) {
        usersServiceImp.deleteUser(username);
        return ResponseEntity.ok(new microservices.order_processing.order_service.controllers.responses.ApiResponse(true, "User " + username + " has been deleted successfully!"));
    }
}
