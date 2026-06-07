import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

public class RoundedBorder extends AbstractBorder {

    private final Color color;
    private final int thickness;
    private final int radius;
    private final Insets insets;

    public RoundedBorder(Color color, int thickness, int radius) {
        this.color = Objects.requireNonNullElse(color, Color.BLACK);
        this.thickness = Math.max(1, thickness);
        this.radius = Math.max(0, radius);
        this.insets = new Insets(radius, radius, radius, radius);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));

        double adjust = thickness / 2.0;

        g2.draw(new RoundRectangle2D.Double(
                x + adjust,
                y + adjust,
                width - thickness,
                height - thickness,
                radius,
                radius
        ));

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = this.insets.left;
        insets.top = this.insets.top;
        insets.right = this.insets.right;
        insets.bottom = this.insets.bottom;
        return insets;
    }
}