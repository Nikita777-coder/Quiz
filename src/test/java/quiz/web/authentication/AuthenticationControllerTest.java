package quiz.web.authentication;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import quiz.web.authentication.model.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUpAuthenticationController() {
        authenticationController = new AuthenticationController();
    }

    @Test
    void authenticateUser() {
    }

    @Test
    @DisplayName("AuthenticationControllerTest->registerUserWithEmptyData; must ")
    void registerUserWithEmptyData() {
        authenticationController.registerUser(new RegisterRequest("", ""));
    }
}