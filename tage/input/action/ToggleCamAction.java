package tage.input.action;

import a1.MyGame;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import tage.GameObject;
import tage.input.action.AbstractInputAction;

// action class for toggling camera between on and off the avatar
public class ToggleCamAction extends AbstractInputAction {

  private MyGame game;
  private GameObject av;
  private Camera cam;
  private boolean up;

  public ToggleCamAction(MyGame g) {
    game = g;
  }

  @Override
  public void performAction(float time, Event e) {
    game.toggleCam();
  }
}
