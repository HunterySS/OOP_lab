import shapes.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


public class ShapeEditor extends JFrame {
    private ShapeList shapeList;

    private DrawingPanel drawingPanel;
    private JLabel statusLabel;
    private JComboBox<String> shapeTypeCombo;
    private JLabel instructionLabel;

    private List<ShapeFactory> factories;

    private ShapeCreationState creationState;
    private List<Point> currentPoints;

    /**
     * Конструктор
     */
    public ShapeEditor() {
        setTitle("Графический редактор - создание фигур мышкой");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        shapeList = new ShapeList();
        factories = new ArrayList<>();
        currentPoints = new ArrayList<>();
        creationState = ShapeCreationState.IDLE;

        initializeFactories();

        initializeUI();

        addDemoShapes();
    }

    /**
     * Регистрация всех фабрик фигур
     */
    private void initializeFactories() {
        factories.add(new LineFactory());
        factories.add(new MyRectangleFactory());
        factories.add(new EllipseFactory());
        factories.add(new CircleFactory());
        factories.add(new SquareFactory());
        factories.add(new TriangleFactory());
    }

    /**
     * Добавление демонстрационных фигур
     */
    private void addDemoShapes() {
        shapeList.addShape(new Line(100, 100, 200, 100));
        shapeList.addShape(new MyRectangle(250, 80, 120, 80));
        shapeList.addShape(new Ellipse(100, 220, 150, 80));
        shapeList.addShape(new Circle(500, 150, 50));
        shapeList.addShape(new Square(120, 350, 80));
        shapeList.addShape(new Triangle(450, 350, 500, 280, 550, 350));
    }

    /**
     * Создание пользовательского интерфейса
     */
    private void initializeUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Выберите фигуру:"));

        String[] shapeNames = factories.stream()
                .map(ShapeFactory::getShapeName)
                .toArray(String[]::new);
        shapeTypeCombo = new JComboBox<>(shapeNames);
        shapeTypeCombo.addActionListener(e -> resetCreationState());
        controlPanel.add(shapeTypeCombo);

        JButton clearButton = new JButton("Очистить всё");
        clearButton.addActionListener(e -> {
            shapeList.clear();
            resetCreationState();
            drawingPanel.repaint();
            updateStatus();
        });
        controlPanel.add(clearButton);

        topPanel.add(controlPanel, BorderLayout.WEST);

        instructionLabel = new JLabel(" Режим: ожидание выбора фигуры");
        instructionLabel.setForeground(new Color(0, 100, 0));
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(instructionLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setPreferredSize(new Dimension(900, 550));
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getPoint());
            }
        };
        drawingPanel.addMouseListener(mouseHandler);
        drawingPanel.addMouseMotionListener(mouseHandler);

        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);

        statusLabel = new JLabel(" Готов. Всего фигур: " + shapeList.size());
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Сброс состояния создания фигуры
     */
    private void resetCreationState() {
        currentPoints.clear();
        creationState = ShapeCreationState.IDLE;
        updateInstruction();
        drawingPanel.repaint();
    }

    /**
     * Обновление текста инструкции
     */
    private void updateInstruction() {
        String shapeName = (String) shapeTypeCombo.getSelectedItem();
        switch (creationState) {
            case IDLE:
                instructionLabel.setText(" Режим: выберите фигуру и кликните для начала создания");
                break;
            case LINE_FIRST_POINT:
                instructionLabel.setText(" Создание линии: кликните для задания начальной точки");
                break;
            case LINE_SECOND_POINT:
                instructionLabel.setText(" Создание линии: кликните для задания конечной точки");
                break;
            case RECTANGLE_FIRST_POINT:
                instructionLabel.setText(" Создание прямоугольника: кликните для задания первого угла");
                break;
            case RECTANGLE_SECOND_POINT:
                instructionLabel.setText(" Создание прямоугольника: кликните для задания второго угла");
                break;
            case ELLIPSE_FIRST_POINT:
                instructionLabel.setText(" Создание эллипса: кликните для задания первого угла описанного прямоугольника");
                break;
            case ELLIPSE_SECOND_POINT:
                instructionLabel.setText(" Создание эллипса: кликните для задания второго угла");
                break;
            case CIRCLE_CENTER:
                instructionLabel.setText(" Создание круга: кликните для задания центра");
                break;
            case CIRCLE_RADIUS:
                instructionLabel.setText(" Создание круга: кликните для задания точки на окружности");
                break;
            case SQUARE_FIRST_POINT:
                instructionLabel.setText(" Создание квадрата: кликните для задания первого угла");
                break;
            case SQUARE_SECOND_POINT:
                instructionLabel.setText(" Создание квадрата: кликните для задания второго угла");
                break;
            case TRIANGLE_POINT1:
                instructionLabel.setText(" Создание треугольника: кликните для задания первой вершины (осталось 3)");
                break;
            case TRIANGLE_POINT2:
                instructionLabel.setText(" Создание треугольника: кликните для задания второй вершины (осталось 2)");
                break;
            case TRIANGLE_POINT3:
                instructionLabel.setText(" Создание треугольника: кликните для задания третьей вершины (осталась 1)");
                break;
        }
        instructionLabel.setForeground(new Color(0, 100, 0));
    }

    /**
     * Обработка кликов мыши
     */
    private void handleMouseClick(Point clickPoint) {
        String selectedShape = (String) shapeTypeCombo.getSelectedItem();

        if (creationState == ShapeCreationState.IDLE) {
            startShapeCreation(selectedShape, clickPoint);
        } else {
            continueShapeCreation(selectedShape, clickPoint);
        }

        drawingPanel.repaint();
        updateStatus();
    }

    /**
     * Начало создания новой фигуры
     */
    private void startShapeCreation(String shapeName, Point point) {
        currentPoints.clear();
        currentPoints.add(point);

        switch (shapeName) {
            case "Line":
                creationState = ShapeCreationState.LINE_FIRST_POINT;
                updateInstruction();
                break;
            case "Rectangle":
                creationState = ShapeCreationState.RECTANGLE_FIRST_POINT;
                updateInstruction();
                break;
            case "Ellipse":
                creationState = ShapeCreationState.ELLIPSE_FIRST_POINT;
                updateInstruction();
                break;
            case "Circle":
                creationState = ShapeCreationState.CIRCLE_CENTER;
                updateInstruction();
                break;
            case "Square":
                creationState = ShapeCreationState.SQUARE_FIRST_POINT;
                updateInstruction();
                break;
            case "Triangle":
                creationState = ShapeCreationState.TRIANGLE_POINT1;
                updateInstruction();
                break;
        }
    }

    /**
     * Продолжение создания фигуры
     */
    private void continueShapeCreation(String shapeName, Point point) {
        currentPoints.add(point);

        switch (creationState) {
            case LINE_FIRST_POINT:
                creationState = ShapeCreationState.LINE_SECOND_POINT;
                updateInstruction();
                break;

            case LINE_SECOND_POINT:
                if (currentPoints.size() >= 2) {
                    Point p1 = currentPoints.get(0);
                    Point p2 = currentPoints.get(1);
                    shapeList.addShape(new Line(p1.x, p1.y, p2.x, p2.y));
                    resetCreationState();
                }
                break;

            case RECTANGLE_FIRST_POINT:
                creationState = ShapeCreationState.RECTANGLE_SECOND_POINT;
                updateInstruction();
                break;

            case RECTANGLE_SECOND_POINT:
                if (currentPoints.size() >= 2) {
                    Point p1 = currentPoints.get(0);
                    Point p2 = currentPoints.get(1);
                    int x = Math.min(p1.x, p2.x);
                    int y = Math.min(p1.y, p2.y);
                    int width = Math.abs(p2.x - p1.x);
                    int height = Math.abs(p2.y - p1.y);
                    if (width > 0 && height > 0) {
                        shapeList.addShape(new MyRectangle(x, y, width, height));
                    }
                    resetCreationState();
                }
                break;

            case ELLIPSE_FIRST_POINT:
                creationState = ShapeCreationState.ELLIPSE_SECOND_POINT;
                updateInstruction();
                break;

            case ELLIPSE_SECOND_POINT:
                if (currentPoints.size() >= 2) {
                    Point p1 = currentPoints.get(0);
                    Point p2 = currentPoints.get(1);
                    int x = Math.min(p1.x, p2.x);
                    int y = Math.min(p1.y, p2.y);
                    int width = Math.abs(p2.x - p1.x);
                    int height = Math.abs(p2.y - p1.y);
                    if (width > 0 && height > 0) {
                        shapeList.addShape(new Ellipse(x, y, width, height));
                    }
                    resetCreationState();
                }
                break;

            case CIRCLE_CENTER:
                creationState = ShapeCreationState.CIRCLE_RADIUS;
                updateInstruction();
                break;

            case CIRCLE_RADIUS:
                if (currentPoints.size() >= 2) {
                    Point center = currentPoints.get(0);
                    Point radiusPoint = currentPoints.get(1);
                    int radius = (int) Math.sqrt(
                            Math.pow(radiusPoint.x - center.x, 2) +
                                    Math.pow(radiusPoint.y - center.y, 2)
                    );
                    if (radius > 0) {
                        shapeList.addShape(new Circle(
                                center.x - radius,
                                center.y - radius,
                                radius
                        ));
                    }
                    resetCreationState();
                }
                break;

            case SQUARE_FIRST_POINT:
                creationState = ShapeCreationState.SQUARE_SECOND_POINT;
                updateInstruction();
                break;

            case SQUARE_SECOND_POINT:
                if (currentPoints.size() >= 2) {
                    Point p1 = currentPoints.get(0);
                    Point p2 = currentPoints.get(1);
                    int side = Math.max(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
                    int x = p1.x;
                    int y = p1.y;
                    if (p2.x < p1.x) x = p1.x - side;
                    if (p2.y < p1.y) y = p1.y - side;
                    shapeList.addShape(new Square(x, y, side));
                    resetCreationState();
                }
                break;

            case TRIANGLE_POINT1:
                creationState = ShapeCreationState.TRIANGLE_POINT2;
                updateInstruction();
                break;

            case TRIANGLE_POINT2:
                creationState = ShapeCreationState.TRIANGLE_POINT3;
                updateInstruction();
                break;

            case TRIANGLE_POINT3:
                if (currentPoints.size() >= 3) {
                    Point p1 = currentPoints.get(0);
                    Point p2 = currentPoints.get(1);
                    Point p3 = currentPoints.get(2);
                    shapeList.addShape(new Triangle(
                            p1.x, p1.y, p2.x, p2.y, p3.x, p3.y
                    ));
                    resetCreationState();
                }
                break;
        }
    }

    /**
     * Обработка движения мыши (для предпросмотра)
     */
    private void handleMouseMove(Point mousePos) {
        if (creationState != ShapeCreationState.IDLE) {
            drawingPanel.setMousePosition(mousePos);
            drawingPanel.repaint();
        }
    }

    /**
     * Обновление строки состояния
     */
    private void updateStatus() {
        statusLabel.setText(" Всего фигур: " + shapeList.size() +
                " | Режим: создание через клики мышкой");
    }

    /**
     * Перечисление состояний создания фигур
     */
    private enum ShapeCreationState {
        IDLE,
        LINE_FIRST_POINT, LINE_SECOND_POINT,
        RECTANGLE_FIRST_POINT, RECTANGLE_SECOND_POINT,
        ELLIPSE_FIRST_POINT, ELLIPSE_SECOND_POINT,
        CIRCLE_CENTER, CIRCLE_RADIUS,
        SQUARE_FIRST_POINT, SQUARE_SECOND_POINT,
        TRIANGLE_POINT1, TRIANGLE_POINT2, TRIANGLE_POINT3
    }

    /**
     * Внутренний класс панели рисования
     */
    private class DrawingPanel extends JPanel {
        private Point mousePosition = null;

        public void setMousePosition(Point pos) {
            this.mousePosition = pos;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Рисуем сетку
            drawGrid(g2d);

            // Рисуем все фигуры
            g2d.setColor(Color.BLUE);
            shapeList.drawAll(g2d);

            // Рисуем точки для текущего создания
            drawCreationPreview(g2d);

            // Рисуем координатную сетку
            drawCoordinateAxes(g2d);
        }

        /**
         * Рисование предпросмотра создания фигуры
         */
        private void drawCreationPreview(Graphics2D g2d) {
            if (creationState == ShapeCreationState.IDLE || currentPoints.isEmpty()) {
                return;
            }

            g2d.setColor(new Color(255, 100, 100, 150));
            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{5, 5}, 0));

            // Рисуем уже поставленные точки
            for (Point p : currentPoints) {
                g2d.fillOval(p.x - 4, p.y - 4, 8, 8);
                g2d.drawString("(" + p.x + ", " + p.y + ")", p.x + 10, p.y - 10);
            }

            if (mousePosition != null) {
                g2d.setColor(new Color(255, 0, 0, 100));

                switch (creationState) {
                    case LINE_FIRST_POINT:
                        // Ждем первую точку
                        break;

                    case LINE_SECOND_POINT:
                        if (currentPoints.size() >= 1) {
                            Point p1 = currentPoints.get(0);
                            g2d.drawLine(p1.x, p1.y, mousePosition.x, mousePosition.y);
                        }
                        break;

                    case RECTANGLE_SECOND_POINT:
                    case ELLIPSE_SECOND_POINT:
                        if (currentPoints.size() >= 1) {
                            Point p1 = currentPoints.get(0);
                            int x = Math.min(p1.x, mousePosition.x);
                            int y = Math.min(p1.y, mousePosition.y);
                            int w = Math.abs(mousePosition.x - p1.x);
                            int h = Math.abs(mousePosition.y - p1.y);

                            if (creationState == ShapeCreationState.RECTANGLE_SECOND_POINT) {
                                g2d.drawRect(x, y, w, h);
                            } else {
                                g2d.drawOval(x, y, w, h);
                            }
                        }
                        break;

                    case CIRCLE_RADIUS:
                        if (currentPoints.size() >= 1) {
                            Point center = currentPoints.get(0);
                            int radius = (int) Math.sqrt(
                                    Math.pow(mousePosition.x - center.x, 2) +
                                            Math.pow(mousePosition.y - center.y, 2)
                            );
                            g2d.drawOval(center.x - radius, center.y - radius,
                                    radius * 2, radius * 2);
                            g2d.drawLine(center.x, center.y, mousePosition.x, mousePosition.y);
                        }
                        break;

                    case SQUARE_SECOND_POINT:
                        if (currentPoints.size() >= 1) {
                            Point p1 = currentPoints.get(0);
                            int side = Math.max(
                                    Math.abs(mousePosition.x - p1.x),
                                    Math.abs(mousePosition.y - p1.y)
                            );
                            int x = p1.x;
                            int y = p1.y;
                            if (mousePosition.x < p1.x) x = p1.x - side;
                            if (mousePosition.y < p1.y) y = p1.y - side;
                            g2d.drawRect(x, y, side, side);
                        }
                        break;

                    case TRIANGLE_POINT2:
                        if (currentPoints.size() >= 1) {
                            Point p1 = currentPoints.get(0);
                            g2d.drawLine(p1.x, p1.y, mousePosition.x, mousePosition.y);
                        }
                        break;

                    case TRIANGLE_POINT3:
                        if (currentPoints.size() >= 2) {
                            Point p1 = currentPoints.get(0);
                            Point p2 = currentPoints.get(1);
                            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                            g2d.drawLine(p2.x, p2.y, mousePosition.x, mousePosition.y);
                            g2d.drawLine(mousePosition.x, mousePosition.y, p1.x, p1.y);
                        }
                        break;
                }
            }
        }

        /**
         * Рисование сетки
         */
        private void drawGrid(Graphics2D g2d) {
            g2d.setColor(new Color(230, 230, 230));
            g2d.setStroke(new BasicStroke(0.5f));

            for (int x = 0; x < getWidth(); x += 20) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y < getHeight(); y += 20) {
                g2d.drawLine(0, y, getWidth(), y);
            }
        }

        /**
         * Рисование осей координат
         */
        private void drawCoordinateAxes(Graphics2D g2d) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
            g2d.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        }
    }

    /**
     * Точка входа
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ShapeEditor().setVisible(true);
        });
    }
}