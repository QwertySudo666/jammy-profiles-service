package jammy.platform.exceptions;

public class ProfileAccessDeniedException extends RuntimeException {
  public ProfileAccessDeniedException(String message) {
    super(message);
  }
}
