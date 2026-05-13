package jammy.platform.exceptions;

public class ProfileAlreadyExistsException extends RuntimeException {
  public ProfileAlreadyExistsException() {
    super("Profile already exists");
  }
}
