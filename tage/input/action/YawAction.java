package tage.input.action;

import a1.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

public class YawAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam, c;
  private Vector3f rightVector, upVector, fwdVector;
  private int right = -1;

  // constructor for controllers
  public YawAction(MyGame g) {
    game = g;
  }

  // constructor for keyboards
  // int input tells us whether we want to turn right or left
  // input should be 0 for right or 1 for left
  // handling of this input is done in the game object and camera classes
  public YawAction(MyGame g, int input) {
    game = g;
    right = input;
  }

  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    // if the camera is on the avatar, turn the avatar and reposition camera
    if (game.isCamOnAv()) {
      av = game.getAvatar();
      av.yaw(right, time, keyValue);
      game.positionCameraOnAv();
    // else turn camera
    } else {
      cam = game.getCamera();
      cam.yaw(right, time, keyValue);
    }
  }
}
