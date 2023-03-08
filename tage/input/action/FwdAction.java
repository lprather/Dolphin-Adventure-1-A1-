package tage.input.action;

import a1.MyGame;
import net.java.games.input.Event;
import tage.*;
//import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;
import tage.rml.*;

public class FwdAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam;
  private boolean xClose, yClose, zClose, avAndCamClose;
  private int pos = -1;

  // constructor for controllers
  public FwdAction(MyGame g) {
    game = g;
  }

  // constructor for keyboards
  // int input tells us whether we want to move forward or backwards
  // input should be 0 for forward or 1 for backwards
  // handling of this input is done in the game object and camera classes
  public FwdAction(MyGame g, int input) {
    game = g;
    pos = input;
  }

  @Override
  public void performAction(float time, Event e) {
    float keyValue = e.getValue();
    if (keyValue > -.2 && keyValue < .2) return; // deadzone

    // if the camera is on the avatar, move the avatar and resposition camera
    if (game.isCamOnAv()) {
      av = game.getAvatar();
      av.fwd(pos, time, keyValue);
      game.positionCameraOnAv();
    // else move the camera
    } else {
      av = game.getAvatar();
      cam = game.getCamera();

      // check whether x,y,z of the camera are close enough to the avatar
      // being too far from the avatar means the camera should be unable to move
      xClose =
        (Math.abs(av.getWorldLocation().x() - cam.getLocation().x())) < 5;
      yClose =
        (Math.abs(av.getWorldLocation().y() - cam.getLocation().y())) < 5;
      zClose =
        (Math.abs(av.getWorldLocation().z() - cam.getLocation().z())) < 5;
      avAndCamClose = xClose && yClose && zClose;

      cam.fwd(pos, avAndCamClose, time, keyValue);
    }
  }
}
