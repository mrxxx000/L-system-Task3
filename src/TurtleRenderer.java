import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Stack;

public class TurtleRenderer {
    private final int width;
    private final int height;
    private final BufferedImage img;
    private final Graphics2D g;

    public TurtleRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = img.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0,width,height);
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(1.2f));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Rectangle2D renderToFile(String commands, double startX, double startY,
                                    double startAngleDeg, double step, double angleDeg,
                                    String filename) throws Exception {
        double x = startX;
        double y = startY;
        double angle = Math.toRadians(startAngleDeg);
        Stack<State> stack = new Stack<>();
        Path2D path = new Path2D.Double();
        path.moveTo(x,y);
        double minX = x, maxX = x, minY = y, maxY = y;

        for (char c : commands.toCharArray()) {
            switch (c) {
                case 'F':
                    double nx = x + Math.cos(angle) * step;
                    double ny = y + Math.sin(angle) * step;
                    g.draw(new Line2D.Double(x, y, nx, ny));
                    x = nx; y = ny;
                    minX = Math.min(minX, x); maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y); maxY = Math.max(maxY, y);
                    break;
                case 'f': 
                    x += Math.cos(angle) * step;
                    y += Math.sin(angle) * step;
                    minX = Math.min(minX, x); maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y); maxY = Math.max(maxY, y);
                    break;
                case '+':
                    angle += Math.toRadians(angleDeg);
                    break;
                case '-':
                    angle -= Math.toRadians(angleDeg);
                    break;
                case '[':
                    stack.push(new State(x,y,angle));
                    break;
                case ']':
                    if (!stack.isEmpty()) {
                        State s = stack.pop();
                        x = s.x; y = s.y; angle = s.angle;
                    }
                    break;
                default:
                    break;
            }
        }

        ImageIO.write(img, "PNG", new File(filename));
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    private static class State {
        double x,y,angle;
        State(double x,double y,double a){this.x=x;this.y=y;this.angle=a;}
    }
}
