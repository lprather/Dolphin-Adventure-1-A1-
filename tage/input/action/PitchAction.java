package tage.input.action;

import a1.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

// action class for pitching game object or camera up/down
public class PitchAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam;
  private boolean up;

  // constructor for controllers
  public PitchAction(MyGame g) {
    game = g;
  }

  // constructor for keyboard inputs
  // boolean input determines whether the key is bound to pitch up or down
  public PitchAction(MyGame g, boolean input) {
    game = g;
    up = input;
  }

  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    // calling pitch on either the game object or the camera requires
    // the passing of a boolean to tell us if we are pitching up or down
    // for controllers, the boolean has not been initialized and will
    // pitch based on the keyValue being positive or negative instead

    // if the camera is on the avatar, pitch avatar and reposition camera
    if (game.isCamOnAv()) {
      av = game.getAvatar();
      av.pitch(up, keyValue, time);
      game.positionCameraOnAv();
    // else, pitch camera
    } else {
      cam = game.getCamera();
      cam.pitch(up, time, keyValue);
    }
  }
}
