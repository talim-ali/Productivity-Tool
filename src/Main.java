import dao.TaskDAO;
import model.Task;
import exception.TaskValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main extends JFrame {

    // ===== Premium neon palette =====
    static final Color BG = new Color(7, 12, 38);
    static final Color SIDEBAR = new Color(8, 16, 48);
    static final Color PANEL = new Color(16, 25, 62);
    static final Color PANEL_2 = new Color(21, 31, 74);
    static final Color WHITE = new Color(248, 250, 255);
    static final Color MUTED = new Color(169, 181, 215);

    static final Color BLUE = new Color(45, 132, 255);
    static final Color CYAN = new Color(0, 214, 255);
    static final Color PURPLE = new Color(139, 65, 255);
    static final Color MAGENTA = new Color(255, 55, 175);
    static final Color PINK = new Color(255, 77, 137);
    static final Color GREEN = new Color(0, 220, 158);
    static final Color ORANGE = new Color(255, 157, 54);
    static final Color RED = new Color(255, 76, 105);

    // ===== User name is entered when the application starts =====
    private static String USER_NAME = "User";

    private final TaskDAO taskDAO = new TaskDAO();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);

    private JLabel totalValue, completedValue, pendingValue, productivityValue;
    private JPanel taskList;
    private JTextField taskSearch;
    private String taskFilter = "All";

    // ===== Pomodoro engine (multithreading + synchronization) =====
    private PomodoroEngine pomodoroEngine;
    private CircularTimer pomodoroTimer;
    private JLabel pomodoroTimeLabel;
    private JButton pomodoroStartButton;

    public Main() {
        setTitle("Productivity Tool");
        setSize(1400, 800);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        pages.setBackground(BG);
        pages.add(createDashboard(), "Dashboard");
        pages.add(createTasksPage(), "Tasks");
        pages.add(createPomodoroPage(), "Pomodoro");
        pages.add(createCalendarPage(), "Calendar");
        pages.add(createProgressPage(), "Progress");
        pages.add(createAnalyticsPage(), "Analytics");
        pages.add(createSettingsPage(), "Settings");

        add(pages, BorderLayout.CENTER);
        showPage("Dashboard");
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {
        JPanel side = new GradientPanel(SIDEBAR, new Color(13, 19, 58));
        side.setPreferredSize(new Dimension(220, 0));
        side.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(25, 20, 10, 20));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);

        GlowIcon icon = new GlowIcon("✓", new Color(0, 224, 255), 46);
        brand.add(icon);

        JPanel brandText = new JPanel();
        brandText.setOpaque(false);
        brandText.setLayout(new BoxLayout(brandText, BoxLayout.Y_AXIS));

        JLabel title = label("Productivity Tool", 17, Font.BOLD, WHITE);
        JLabel sub = label("Plan  •  Focus  •  Achieve", 10, Font.PLAIN, MUTED);

        brandText.add(title);
        brandText.add(Box.createVerticalStrut(3));
        brandText.add(sub);
        brand.add(brandText);

        top.add(brand);
        top.add(Box.createVerticalStrut(35));

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        addMenu(menu, "⌂", "Home", "Dashboard", true);
        addMenu(menu, "✓", "Tasks", "Tasks", false);
        addMenu(menu, "◷", "Pomodoro", "Pomodoro", false);
        addMenu(menu, "□", "Calendar", "Calendar", false);
        addMenu(menu, "↗", "Progress", "Progress", false);
        addMenu(menu, "▥", "Analytics", "Analytics", false);
        addMenu(menu, "⚙", "Settings", "Settings", false);

        top.add(menu);
        side.add(top, BorderLayout.NORTH);

        JPanel quote = new GlassPanel(20);
        quote.setLayout(new BoxLayout(quote, BoxLayout.Y_AXIS));
        quote.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel q1 = label("✦", 25, Font.BOLD, MAGENTA);
        JLabel q2 = label("<html>“Discipline today<br>leads to success<br>tomorrow.”</html>",
                13, Font.BOLD, WHITE);

        quote.add(q1);
        quote.add(Box.createVerticalStrut(10));
        quote.add(q2);

        JPanel quoteWrap = new JPanel(new BorderLayout());
        quoteWrap.setOpaque(false);
        quoteWrap.setBorder(new EmptyBorder(15, 18, 18, 18));
        quoteWrap.add(quote);

        side.add(quoteWrap, BorderLayout.CENTER);

        // ===== Fixed bottom profile area =====
        // Keep the profile in a dedicated fixed-height container so the
        // name and "Student" label can never be pushed below the window.
        JPanel profile = new JPanel(new BorderLayout(12, 0));
        profile.setOpaque(false);
        profile.setBorder(new EmptyBorder(8, 18, 12, 18));
        profile.setPreferredSize(new Dimension(220, 92));
        profile.setMinimumSize(new Dimension(220, 92));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        GlowIcon avatar = new GlowIcon(getInitial(), MAGENTA, 40);
        profile.add(avatar, BorderLayout.WEST);

        JPanel pText = new JPanel();
        pText.setOpaque(false);
        pText.setLayout(new BoxLayout(pText, BoxLayout.Y_AXIS));

        JLabel profileName = label(USER_NAME, 12, Font.BOLD, WHITE);
        JLabel profileRole = label("Student", 10, Font.PLAIN, MUTED);
        profileName.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        pText.setBorder(new EmptyBorder(8, 0, 0, 0));
        pText.add(profileName);
        pText.add(Box.createVerticalStrut(4));
        pText.add(profileRole);

        profile.add(pText, BorderLayout.CENTER);

        JPanel profileWrap = new JPanel(new BorderLayout());
        profileWrap.setOpaque(false);
        profileWrap.setPreferredSize(new Dimension(220, 102));
        profileWrap.setMinimumSize(new Dimension(220, 102));
        profileWrap.add(profile, BorderLayout.CENTER);

        side.add(profileWrap, BorderLayout.SOUTH);

        return side;
    }

    private void addMenu(JPanel menu, String icon, String text, String page, boolean selected) {
        NeonMenuButton b = new NeonMenuButton(icon, text, selected);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(e -> showPage(page));
        menu.add(b);
        menu.add(Box.createVerticalStrut(7));
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    /**
     * Creates a responsive row. Each component gets an equal share of the
     * available width and is allowed to shrink when the window is resized.
     * This prevents the right side of the dashboard from being clipped.
     */
    private JPanel responsiveRow(int columns, int height, JComponent... components) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setPreferredSize(new Dimension(0, height));
        row.setMinimumSize(new Dimension(0, height));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        for (int i = 0; i < components.length; i++) {
            JComponent component = components[i];
            component.setMinimumSize(new Dimension(0, 0));
            component.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = i;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(0, i == 0 ? 0 : 7, 0, i == columns - 1 ? 0 : 7);
            row.add(component, c);
        }

        return row;
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();

        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    private String getInitial() {
        if (USER_NAME == null || USER_NAME.trim().isEmpty()) {
            return "U";
        }
        return USER_NAME.trim().substring(0, 1).toUpperCase();
    }

    private JPanel createDashboard() {
        JPanel root = pagePanel();

        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel greet = new JPanel();
        greet.setOpaque(false);
        greet.setLayout(new BoxLayout(greet, BoxLayout.Y_AXIS));
        greet.add(label(getGreeting() + ", " + USER_NAME, 30, Font.BOLD, WHITE));
        greet.add(Box.createVerticalStrut(5));
        greet.add(label("Stay consistent. Progress over perfection.", 13, Font.PLAIN, MUTED));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
       JTextField search = searchBox("Search tasks, notes...");

search.addActionListener(e -> {
    taskSearch.setText(search.getText());
    showPage("Tasks");
});

right.add(searchFieldWrap(search));
right.add(label("•", 24, Font.BOLD, WHITE));
right.add(new GlowIcon(getInitial(), MAGENTA, 38));

        header.add(greet, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(Box.createVerticalStrut(18));

        JPanel hero = createHero();
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        content.add(hero);

        content.add(Box.createVerticalStrut(15));
        JPanel stats = createStats();
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(stats);

        content.add(Box.createVerticalStrut(15));
        content.add(responsiveRow(3, 275,
                createFocusCard(),
                createPomodoroMiniCard(),
                createCalendarMiniCard()));

        content.add(Box.createVerticalStrut(15));
        content.add(responsiveRow(3, 235,
                createWeeklyChart(),
                createQuickActions(),
                createDistribution()));
        content.add(Box.createVerticalStrut(30));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().setBackground(BG);
        scroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);

        refreshDashboard();
        return root;
    }

    private JPanel createHero() {
        HeroPanel hero = new HeroPanel();
        hero.setPreferredSize(new Dimension(0, 220));
        hero.setMinimumSize(new Dimension(0, 180));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        hero.setBorder(new EmptyBorder(25, 32, 25, 32));
        hero.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        text.add(label(getGreeting() + ",", 34, Font.BOLD, WHITE));
        text.add(label(USER_NAME + " 👋", 34, Font.BOLD, WHITE));
        text.add(Box.createVerticalStrut(7));
        text.add(label("A productive day is a collection of small wins.", 13, Font.PLAIN, WHITE));
        text.add(Box.createVerticalStrut(18));

        JPanel quote = new GlassPanel(18);
        quote.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        quote.add(label("❝", 24, Font.BOLD, MAGENTA));
        quote.add(label("Same Focus  •  Bigger Results", 12, Font.BOLD, WHITE));
        text.add(quote);

        hero.add(text, BorderLayout.WEST);
        hero.add(label("☀", 42, Font.PLAIN, ORANGE), BorderLayout.EAST);

        return hero;
    }

    private JPanel createStats() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setPreferredSize(new Dimension(0, 118));
        stats.setMinimumSize(new Dimension(0, 118));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        totalValue = label("0", 23, Font.BOLD, WHITE);
        completedValue = label("0", 23, Font.BOLD, WHITE);
        pendingValue = label("0", 23, Font.BOLD, WHITE);
        productivityValue = label("0%", 23, Font.BOLD, WHITE);

        stats.add(statCard("T", "Total Tasks", totalValue, BLUE, "+3 this week"));
        stats.add(statCard("✓", "Completed", completedValue, GREEN, "completion rate"));
        stats.add(statCard("◷", "Pending", pendingValue, ORANGE, "needs attention"));
        stats.add(statCard("%", "Productivity", productivityValue, PURPLE, "overall progress"));

        return stats;
    }

    private JPanel statCard(String icon, String title, JLabel value, Color color, String footer) {
        GlassPanel p = new GlassPanel(20);
        p.setLayout(new BorderLayout(10, 0));
        p.setBorder(new EmptyBorder(15, 12, 15, 12));
        p.setMinimumSize(new Dimension(0, 100));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        GlowIcon i = new GlowIcon(icon, color, 48);
        p.add(i, BorderLayout.WEST);

        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(label(title, 11, Font.PLAIN, MUTED));
        t.add(Box.createVerticalStrut(3));
        t.add(value);
        t.add(Box.createVerticalStrut(5));
        t.add(label("↗ " + footer, 10, Font.PLAIN, color));

        p.add(t, BorderLayout.CENTER);
        return p;
    }

    private JPanel createFocusCard() {
        GlassPanel p = new GlassPanel(20);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.setLayout(new BorderLayout());

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(label("🎯  Today's Focus", 16, Font.BOLD, WHITE), BorderLayout.WEST);

        JButton view = flatButton("View All →", BLUE);
        view.addActionListener(e -> showPage("Tasks"));
        head.add(view, BorderLayout.EAST);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<Task> tasks = taskDAO.getAllTasks();
        int count = 0;
        for (Task task : tasks) {
            if (count++ >= 4) break;
            list.add(focusRow(task));
            list.add(Box.createVerticalStrut(7));
        }

        if (tasks.isEmpty()) {
            list.add(label("No tasks yet. Add your first task.", 12, Font.PLAIN, MUTED));
        }

        p.add(head, BorderLayout.NORTH);
        p.add(list, BorderLayout.CENTER);
        return p;
    }

    private JPanel focusRow(Task task) {
        JPanel row = new GlassPanel(12);
        row.setBorder(new EmptyBorder(9, 10, 9, 10));
        row.setLayout(new BorderLayout(8, 0));

        JLabel check = label(
                task.getStatus().equalsIgnoreCase("Completed") ? "✓" : "□",
                17, Font.BOLD,
                task.getStatus().equalsIgnoreCase("Completed") ? GREEN : WHITE
        );

        row.add(check, BorderLayout.WEST);
        row.add(label(task.getTaskName(), 11, Font.PLAIN, WHITE), BorderLayout.CENTER);
        row.add(label("⋮", 17, Font.BOLD, MUTED), BorderLayout.EAST);

        return row;
    }

    private JPanel createPomodoroMiniCard() {
        GlassPanel p = new GlassPanel(20);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.setLayout(new BorderLayout());

        p.add(label("⚡  Pomodoro Timer", 16, Font.BOLD, WHITE), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel modes = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 5));
        modes.setOpaque(false);
        modes.add(pill("Pomodoro", PURPLE));
        modes.add(pill("Short Break", PANEL_2));
        modes.add(pill("Long Break", PANEL_2));
        center.add(modes);

        CircularTimer timer = new CircularTimer();
        timer.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(timer);

        JButton start = gradientButton("▶  Start", PURPLE, PINK);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(e -> showPage("Pomodoro"));
        center.add(start);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel createCalendarMiniCard() {
        GlassPanel p = new GlassPanel(20);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.setLayout(new BorderLayout());

        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.add(label("October 2026", 17, Font.BOLD, WHITE), BorderLayout.WEST);
        h.add(label("‹   ›", 20, Font.PLAIN, WHITE), BorderLayout.EAST);

        p.add(h, BorderLayout.NORTH);

        JPanel cal = new CalendarWidget();
        p.add(cal, BorderLayout.CENTER);
        return p;
    }

    private JPanel createWeeklyChart() {
        GlassPanel p = new GlassPanel(20);
        p.setBorder(new EmptyBorder(16, 16, 12, 16));
        p.setLayout(new BorderLayout());

        p.add(label("◉  Task Overview", 16, Font.BOLD, WHITE), BorderLayout.NORTH);
        p.add(new BarChart(), BorderLayout.CENTER);
        return p;
    }

    private JPanel createQuickActions() {
        GlassPanel p = new GlassPanel(20);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.setLayout(new BorderLayout());

        p.add(label("⚡  Quick Actions", 16, Font.BOLD, WHITE), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 9, 9));
        grid.setOpaque(false);

        JButton add = gradientButton("+  Add Task  ›", BLUE, CYAN);
        JButton pomo = gradientButton("◷  Start Pomodoro  ›", PINK, MAGENTA);
        JButton cal = gradientButton("▣  View Calendar  ›", PURPLE, MAGENTA);
        JButton progress = gradientButton("▥  See Progress  ›", GREEN, CYAN);

        add.addActionListener(e -> showAddTaskDialog());
        pomo.addActionListener(e -> showPage("Pomodoro"));
        cal.addActionListener(e -> showPage("Calendar"));
        progress.addActionListener(e -> showPage("Progress"));

        grid.add(add);
        grid.add(pomo);
        grid.add(cal);
        grid.add(progress);

        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel createDistribution() {
        GlassPanel p = new GlassPanel(20);
        p.setBorder(new EmptyBorder(16, 16, 12, 16));
        p.setLayout(new BorderLayout());

        p.add(label("◉  Task Distribution", 16, Font.BOLD, WHITE), BorderLayout.NORTH);
        p.add(new DonutChart(), BorderLayout.CENTER);
        return p;
    }

    // =========================================================
    // TASKS PAGE
    // =========================================================

    private JPanel createTasksPage() {
        JPanel root = pagePanel();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.add(label("Tasks", 30, Font.BOLD, WHITE));
        title.add(Box.createVerticalStrut(5));
        title.add(label("Organize your work and keep moving forward.", 13, Font.PLAIN, MUTED));

        JButton add = gradientButton("+  Add Task", BLUE, PURPLE);
        add.addActionListener(e -> showAddTaskDialog());

        header.add(title, BorderLayout.WEST);
        header.add(add, BorderLayout.EAST);

        JPanel tools = new JPanel(new BorderLayout(10, 0));
        tools.setOpaque(false);
        tools.setBorder(new EmptyBorder(15, 0, 15, 0));

        taskSearch = searchBox("Search tasks...");
        taskSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                refreshTaskList();
            }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        filters.setOpaque(false);

        for (String f : new String[]{"All", "Pending", "Completed"}) {
            JButton b = filterButton(f);
            b.addActionListener(e -> {
                taskFilter = f;
                refreshTaskList();
            });
            filters.add(b);
        }

        JPanel searchWrap = searchFieldWrap(taskSearch);
        tools.add(searchWrap, BorderLayout.WEST);
        tools.add(filters, BorderLayout.EAST);

        taskList = new JPanel();
        taskList.setOpaque(false);
        taskList.setLayout(new BoxLayout(taskList, BoxLayout.Y_AXIS));
        taskList.setBorder(new EmptyBorder(2, 2, 20, 8));
        taskList.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(taskList);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().setBackground(BG);
        scroll.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(tools, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        refreshTaskList();
        return root;
    }

    private JPanel taskCard(Task task) {
        GlassPanel card = new GlassPanel(17);
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setLayout(new GridBagLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMinimumSize(new Dimension(0, 78));
        card.setPreferredSize(new Dimension(0, 86));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));

        boolean completed = task.getStatus() != null &&
                task.getStatus().equalsIgnoreCase("Completed");

        JLabel check = label(completed ? "✓" : "○", 19, Font.BOLD,
                completed ? GREEN : BLUE);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setMinimumSize(new Dimension(0, 55));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel taskName = label(task.getTaskName(), 14, Font.BOLD, WHITE);
        JLabel description = label(
                task.getDescription() == null ? "" : task.getDescription(),
                11, Font.PLAIN, MUTED
        );
        taskName.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(taskName);
        info.add(Box.createVerticalStrut(4));
        info.add(description);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.setMinimumSize(new Dimension(315, 45));
        right.setPreferredSize(new Dimension(315, 45));

        right.add(badge(task.getPriority(), priorityColor(task.getPriority())));
        right.add(badge(task.getStatus(), completed ? GREEN : ORANGE));
        right.add(label(task.getDeadline(), 10, Font.PLAIN, MUTED));

        JButton edit = smallButton("Edit");
        JButton del = smallButton("Delete");

        edit.addActionListener(e -> showEditTaskDialog(task));
        del.addActionListener(e -> deleteTask(task));

        right.add(edit);
        right.add(del);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 12);
        c.gridx = 0;
        card.add(check, c);

        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 0, 10);
        card.add(info, c);

        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(right, c);

        return card;
    }

    private void refreshTaskList() {
        if (taskList == null) return;

        taskList.removeAll();

        List<Task> tasks = taskDAO.getAllTasks();
        String q = taskSearch == null ? "" : taskSearch.getText().trim().toLowerCase();

        int shown = 0;
        for (Task task : tasks) {
            String taskName = task.getTaskName() == null ? "" : task.getTaskName();
            String status = task.getStatus() == null ? "Pending" : task.getStatus();

            boolean filter = taskFilter.equals("All") ||
                    status.equalsIgnoreCase(taskFilter);
            boolean search = taskName.toLowerCase().contains(q);

            if (filter && search) {
                JPanel card = taskCard(task);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
                taskList.add(card);
                taskList.add(Box.createVerticalStrut(10));
                shown++;
            }
        }

        taskList.add(Box.createVerticalStrut(20));

        if (shown == 0) {
            JLabel empty = label("No tasks found.", 15, Font.BOLD, MUTED);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            taskList.add(Box.createVerticalStrut(35));
            taskList.add(empty);
        }

        taskList.revalidate();
        taskList.repaint();
    }

    // =========================================================
    // POMODORO PAGE
    // =========================================================

    private JPanel createPomodoroPage() {
        JPanel root = pagePanel();

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(label("Pomodoro Timer", 30, Font.BOLD, WHITE));
        header.add(Box.createVerticalStrut(5));
        header.add(label("Focus deeply. One session at a time.", 13, Font.PLAIN, MUTED));

        GlassPanel timerCard = new GlassPanel(28);
        timerCard.setLayout(new BoxLayout(timerCard, BoxLayout.Y_AXIS));
        timerCard.setBorder(new EmptyBorder(30, 60, 35, 60));

        JLabel mode = label("POMODORO", 13, Font.BOLD, MAGENTA);
        mode.setAlignmentX(Component.CENTER_ALIGNMENT);

        pomodoroTimer = new CircularTimer();
        pomodoroTimer.setPreferredSize(new Dimension(310, 310));
        pomodoroTimer.setMaximumSize(new Dimension(310, 310));
        pomodoroTimer.setAlignmentX(Component.CENTER_ALIGNMENT);

        pomodoroTimeLabel = label("25:00", 1, Font.BOLD, WHITE);
        pomodoroTimeLabel.setVisible(false); // timer is drawn by CircularTimer

        pomodoroEngine = new PomodoroEngine(25 * 60, new TimerListener() {
            @Override
            public void onTick(long remainingSeconds) {
                SwingUtilities.invokeLater(() -> {
                    pomodoroTimer.setRemainingSeconds(remainingSeconds);
                    updatePomodoroButton();
                });
            }

            @Override
            public void onFinished() {
                SwingUtilities.invokeLater(() -> {
                    pomodoroTimer.setRemainingSeconds(0);
                    updatePomodoroButton();
                    JOptionPane.showMessageDialog(
                            Main.this,
                            "Pomodoro session completed! Great work.",
                            "Focus Session Complete",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });
            }
        });

        pomodoroStartButton = gradientButton("▶  Start Focus", PURPLE, PINK);
        pomodoroStartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pomodoroStartButton.addActionListener(e -> togglePomodoro());

        JButton reset = smallButton("↻ Reset");
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);
        reset.addActionListener(e -> {
            pomodoroEngine.reset();
            pomodoroTimer.setRemainingSeconds(25 * 60);
            updatePomodoroButton();
        });

        timerCard.add(mode);
        timerCard.add(Box.createVerticalStrut(10));
        timerCard.add(pomodoroTimer);
        timerCard.add(Box.createVerticalStrut(10));
        timerCard.add(pomodoroStartButton);
        timerCard.add(Box.createVerticalStrut(8));
        timerCard.add(reset);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(timerCard);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        return root;
    }


    private void togglePomodoro() {
        if (pomodoroEngine == null) return;

        if (pomodoroEngine.isRunning()) {
            pomodoroEngine.pause();
        } else {
            pomodoroEngine.start();
        }
        updatePomodoroButton();
    }

    private void updatePomodoroButton() {
        if (pomodoroStartButton == null || pomodoroEngine == null) return;
        pomodoroStartButton.setText(
                pomodoroEngine.isRunning() ? "Ⅱ  Pause Focus" : "▶  Start Focus"
        );
    }

    // =========================================================
    // CALENDAR PAGE
    // =========================================================

    private JPanel createCalendarPage() {
        JPanel root = pagePanel();

        JPanel header = simpleHeader("Calendar",
                "Plan deadlines and keep your week visible.");

        GlassPanel calendar = new GlassPanel(25);
        calendar.setLayout(new BorderLayout());
        calendar.setBorder(new EmptyBorder(20, 25, 20, 25));
        calendar.add(new LargeCalendar(), BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(calendar, BorderLayout.CENTER);
        return root;
    }

    // =========================================================
    // PROGRESS PAGE
    // =========================================================

    private JPanel createProgressPage() {
        JPanel root = pagePanel();
        root.add(simpleHeader("Progress", "See your productivity at a glance."), BorderLayout.NORTH);

        List<Task> tasks = taskDAO.getAllTasks();
        int total = tasks.size();
        int completed = 0;
        for (Task t : tasks)
            if (t.getStatus().equalsIgnoreCase("Completed")) completed++;

        int percent = total == 0 ? 0 : completed * 100 / total;

        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);
        cards.add(progressCard("Total Tasks", String.valueOf(total), BLUE));
        cards.add(progressCard("Completed", String.valueOf(completed), GREEN));
        cards.add(progressCard("Productivity", percent + "%", PURPLE));

        JPanel charts = new JPanel(new GridLayout(1, 2, 14, 0));
        charts.setOpaque(false);
        charts.add(new GlassPanelWithTitle("Weekly Productivity", new BarChart()));
        charts.add(new GlassPanelWithTitle("Task Distribution", new DonutChart()));

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(charts, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    // =========================================================
    // ANALYTICS PAGE
    // =========================================================

    private JPanel createAnalyticsPage() {
        JPanel root = pagePanel();
        root.add(simpleHeader("Analytics", "Understand where your time and tasks are going."), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        grid.add(new GlassPanelWithTitle("Weekly Task Trend", new BarChart()));
        grid.add(new GlassPanelWithTitle("Task Distribution", new DonutChart()));
        grid.add(new GlassPanelWithTitle("Focus Score", new ProgressRing(78, CYAN)));
        grid.add(new GlassPanelWithTitle("Goal Progress", new ProgressRing(64, MAGENTA)));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    // =========================================================
    // SETTINGS PAGE
    // =========================================================

    private JPanel createSettingsPage() {
        JPanel root = pagePanel();
        root.add(simpleHeader("Settings", "Customize your productivity workspace."), BorderLayout.NORTH);

        GlassPanel settings = new GlassPanel(22);
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        settings.setBorder(new EmptyBorder(25, 25, 25, 25));

        settings.add(label("Appearance", 17, Font.BOLD, WHITE));
        settings.add(Box.createVerticalStrut(15));

        JCheckBox notifications = new JCheckBox("Enable notifications");
        notifications.setOpaque(false);
        notifications.setForeground(WHITE);

        JCheckBox sounds = new JCheckBox("Enable timer sounds");
        sounds.setOpaque(false);
        sounds.setForeground(WHITE);

        settings.add(notifications);
        settings.add(Box.createVerticalStrut(8));
        settings.add(sounds);

        root.add(settings, BorderLayout.NORTH);
        return root;
    }

    // =========================================================
    // TASK DIALOGS
    // =========================================================
     private void validateTaskInput(String name, String deadline)
        throws TaskValidationException {

    if (name == null || name.trim().isEmpty()) {
        throw new TaskValidationException(
                "Task title cannot be empty."
        );
    }

    if (deadline == null || deadline.trim().isEmpty()) {
        throw new TaskValidationException(
                "Due date cannot be empty."
        );
    }

    try {
        LocalDate.parse(deadline.trim());
    } catch (Exception e) {
        throw new TaskValidationException(
                "Invalid due date. Please use YYYY-MM-DD format."
        );
    }
}
    private void showAddTaskDialog() {
        JTextField name = new JTextField();
        JTextField description = new JTextField();
        JTextField deadline = new JTextField(LocalDate.now().toString());

        JComboBox<String> priority =
                new JComboBox<>(new String[]{"High", "Medium", "Low"});

        JPanel form = formPanel();
        form.add(label("Task Title", 12, Font.BOLD, WHITE));
        form.add(name);
        form.add(label("Description", 12, Font.BOLD, WHITE));
        form.add(description);
        form.add(label("Due Date (YYYY-MM-DD)", 12, Font.BOLD, WHITE));
        form.add(deadline);
        form.add(label("Priority", 12, Font.BOLD, WHITE));
        form.add(priority);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Add New Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
           try {
    validateTaskInput(
            name.getText(),
            deadline.getText()
    );
} catch (TaskValidationException e) {
    JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Invalid Task",
            JOptionPane.WARNING_MESSAGE
    );
    return;
}

            taskDAO.addTask(new Task(
                    0,
                    name.getText().trim(),
                    description.getText().trim(),
                    priority.getSelectedItem().toString(),
                    deadline.getText().trim(),
                    "Pending"
            ));

            refreshDashboard();
            refreshTaskList();
        }
    }

    private void showEditTaskDialog(Task task) {
        JTextField name = new JTextField(task.getTaskName());
        JTextField description = new JTextField(task.getDescription());
        JTextField deadline = new JTextField(task.getDeadline());

        JComboBox<String> priority =
                new JComboBox<>(new String[]{"High", "Medium", "Low"});
        priority.setSelectedItem(task.getPriority());

        JComboBox<String> status =
                new JComboBox<>(new String[]{"Pending", "Completed"});
        status.setSelectedItem(task.getStatus());

        JPanel form = formPanel();

        form.add(label("Task Title", 12, Font.BOLD, WHITE));
        form.add(name);
        form.add(label("Description", 12, Font.BOLD, WHITE));
        form.add(description);
        form.add(label("Due Date", 12, Font.BOLD, WHITE));
        form.add(deadline);
        form.add(label("Priority", 12, Font.BOLD, WHITE));
        form.add(priority);
        form.add(label("Status", 12, Font.BOLD, WHITE));
        form.add(status);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

    try {

        validateTaskInput(
                name.getText(),
                deadline.getText()
        );

    } catch (TaskValidationException e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Invalid Task",
                JOptionPane.WARNING_MESSAGE
        );

        return;
    }

    task.setTaskName(name.getText().trim());
    task.setDescription(description.getText().trim());
    task.setDeadline(deadline.getText().trim());
    task.setPriority(priority.getSelectedItem().toString());
    task.setStatus(status.getSelectedItem().toString());

    taskDAO.updateTask(task);

    refreshDashboard();
    refreshTaskList();
}
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.setBackground(PANEL);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setPreferredSize(new Dimension(430, 280));
        return p;
    }

    private void deleteTask(Task task) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + task.getTaskName() + "\"?",
                "Delete Task",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            taskDAO.deleteTask(task.getTaskId());
            refreshTaskList();
            refreshDashboard();
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private JPanel pagePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(22, 24, 22, 24));
        return p;
    }

    private JPanel simpleHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(label(title, 30, Font.BOLD, WHITE));
        p.add(Box.createVerticalStrut(5));
        p.add(label(subtitle, 13, Font.PLAIN, MUTED));
        return p;
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        return l;
    }

    private JPanel searchFieldWrap(JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private JTextField searchBox(String hint) {
        SearchField f = new SearchField(hint);
        f.setPreferredSize(new Dimension(250, 38));
        return f;
    }

    static class SearchField extends JTextField {
        private final String hint;

        SearchField(String hint) {
            this.hint = hint;
            setBackground(new Color(27, 35, 78));
            setForeground(WHITE);
            setCaretColor(WHITE);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(68, 79, 130)),
                    new EmptyBorder(6, 34, 6, 10)
            ));
            setToolTipText(hint);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !hasFocus()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MUTED);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(12, 11, 11, 11);
                g2.drawLine(21, 21, 26, 26);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(hint, 35, 24);
                g2.dispose();
            } else {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MUTED);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(12, 11, 11, 11);
                g2.drawLine(21, 21, 26, 26);
                g2.dispose();
            }
        }
    }

    private JButton gradientButton(String text, Color a, Color b) {
        GradientButton btn = new GradientButton(text, a, b);
        btn.setPreferredSize(new Dimension(155, 40));
        return btn;
    }

    private JButton flatButton(String text, Color color) {
        DarkButton b = new DarkButton(text, BG, color, false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return b;
    }

    private JButton pill(String text, Color color) {
        DarkButton b = new DarkButton(text, color, WHITE, false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return b;
    }

    private JButton filterButton(String text) {
        DarkButton b = new DarkButton(text, PANEL_2, WHITE, true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return b;
    }

    private JButton smallButton(String text) {
        DarkButton b = new DarkButton(text, PANEL_2, WHITE, true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setPreferredSize(new Dimension(text.equals("Delete") ? 62 : 50, 30));
        b.setMinimumSize(new Dimension(text.equals("Delete") ? 62 : 50, 30));
        b.setMaximumSize(new Dimension(text.equals("Delete") ? 62 : 50, 30));
        return b;
    }

    private JLabel badge(String text, Color color) {
        JLabel b = label(text, 10, Font.BOLD, color);
        b.setOpaque(true);
        b.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
        b.setBorder(new EmptyBorder(5, 8, 5, 8));
        return b;
    }

    private JPanel progressCard(String title, String value, Color color) {
        GlassPanel p = new GlassPanel(20);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel t = label(title, 12, Font.PLAIN, MUTED);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel v = label(value, 36, Font.BOLD, color);
        v.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(t);
        p.add(Box.createVerticalStrut(10));
        p.add(v);
        return p;
    }

    private Color priorityColor(String p) {
        if (p == null) return BLUE;
        if (p.equalsIgnoreCase("High")) return RED;
        if (p.equalsIgnoreCase("Medium")) return ORANGE;
        return GREEN;
    }

    private void refreshDashboard() {
        if (totalValue == null) return;

        List<Task> tasks = taskDAO.getAllTasks();
        int total = tasks.size();
        int completed = 0;

        for (Task t : tasks)
            if (t.getStatus().equalsIgnoreCase("Completed"))
                completed++;

        int pending = total - completed;
        int productivity = total == 0 ? 0 : completed * 100 / total;

        totalValue.setText(String.valueOf(total));
        completedValue.setText(String.valueOf(completed));
        pendingValue.setText(String.valueOf(pending));
        productivityValue.setText(productivity + "%");
    }

    private void showPage(String page) {
        if (page.equals("Dashboard")) refreshDashboard();
        if (page.equals("Tasks")) refreshTaskList();
        cardLayout.show(pages, page);
    }

    // =========================================================
    // CUSTOM COMPONENTS
    // =========================================================

    static class GlassPanel extends JPanel {
        private final int radius;

        GlassPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(20, 31, 72, 235));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.setColor(new Color(74, 91, 155, 110));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class GradientPanel extends JPanel {
        private final Color a, b;

        GradientPanel(Color a, Color b) {
            this.a = a;
            this.b = b;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            // Do not call super.paintComponent here: it would paint the panel
            // background over our custom gradient.
        }
    }

    /**
     * Custom dark button. Windows' native Look & Feel can ignore ordinary
     * JButton background colours and paint a white button. This component
     * paints its own background, so Edit/Delete/filter buttons always match
     * the application's dark theme.
     */
    static class DarkButton extends JButton {
        private final Color backgroundColor;
        private final Color textColor;
        private final boolean bordered;

        DarkButton(String text, Color backgroundColor, Color textColor, boolean bordered) {
            super(text);
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            this.bordered = bordered;
            setForeground(textColor);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(0, 8, 0, 8));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = getModel().isRollover()
                    ? backgroundColor.brighter()
                    : backgroundColor;

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

            if (bordered) {
                g2.setColor(new Color(82, 97, 155));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class GradientButton extends JButton {
        private final Color a, b;

        GradientButton(String text, Color a, Color b) {
            super(text);
            this.a = a;
            this.b = b;
            setForeground(WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color left = getModel().isRollover() ? b.brighter() : a;
            Color right = getModel().isRollover() ? a.brighter() : b;

            g2.setPaint(new GradientPaint(0, 0, left, getWidth(), 0, right));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class GlowIcon extends JPanel {
        private final String text;
        private final Color color;
        private final int size;

        GlowIcon(String text, Color color, int size) {
            this.text = text;
            this.color = color;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 45));
            g2.fillRoundRect(0, 0, size, size, 15, 15);

            g2.setColor(color);
            g2.fillRoundRect(5, 5, size - 10, size - 10, 13, 13);

            g2.setColor(WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(15, size / 2)));
            FontMetrics fm = g2.getFontMetrics();
            int x = (size - fm.stringWidth(text)) / 2;
            int y = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x, y);

            g2.dispose();
        }
    }

    static class NeonMenuButton extends JButton {
        private final String icon, text;
        private boolean selected;

        NeonMenuButton(String icon, String text, boolean selected) {
            this.icon = icon;
            this.text = text;
            this.selected = selected;
            setPreferredSize(new Dimension(205, 46));
            setMinimumSize(new Dimension(205, 46));
            setMaximumSize(new Dimension(205, 46));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean hover = getModel().isRollover();

            if (selected || hover) {
                g2.setPaint(new GradientPaint(
                        0, 0,
                        new Color(77, 58, 210),
                        getWidth(), 0,
                        new Color(206, 48, 221)
                ));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 19));
            g2.setColor(WHITE);
            g2.drawString(icon, 17, 30);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(text, 55, 29);

            g2.dispose();
        }
    }

    static class CircularTimer extends JPanel {
        private long remainingSeconds = 25 * 60;
        private final long totalSeconds = 25 * 60;

        CircularTimer() {
            setOpaque(false);
            setPreferredSize(new Dimension(220, 220));
        }

        public void setRemainingSeconds(long seconds) {
            remainingSeconds = Math.max(0, Math.min(totalSeconds, seconds));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight()) - 25;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;

            g2.setStroke(new BasicStroke(13, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(77, 48, 112));
            g2.drawOval(x, y, d, d);

            double progress = 1.0 - (remainingSeconds / (double) totalSeconds);
            int angle = (int) Math.round(360 * progress);

            g2.setPaint(new GradientPaint(
                    x, y, MAGENTA,
                    x + d, y + d, ORANGE
            ));
            g2.drawArc(x, y, d, d, 90, -angle);

            long minutes = remainingSeconds / 60;
            long seconds = remainingSeconds % 60;
            String time = String.format("%02d:%02d", minutes, seconds);

            g2.setColor(WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(time,
                    (getWidth() - fm.stringWidth(time)) / 2,
                    getHeight() / 2 + 8);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String sub = remainingSeconds == 0 ? "Completed" : "Focus Time";
            fm = g2.getFontMetrics();
            g2.drawString(sub,
                    (getWidth() - fm.stringWidth(sub)) / 2,
                    getHeight() / 2 + 31);

            g2.dispose();
        }
    }

    // Interface: separates timer engine from the UI.
    interface TimerListener {
        void onTick(long remainingSeconds);
        void onFinished();
    }

    // Runnable + synchronized state: satisfies the multithreading requirement.
    static class PomodoroEngine implements Runnable {
        private final TimerListener listener;
        private final int initialSeconds;
        private long remainingSeconds;
        private volatile boolean running;
        private Thread worker;

        PomodoroEngine(int seconds, TimerListener listener) {
            this.initialSeconds = seconds;
            this.remainingSeconds = seconds;
            this.listener = listener;
        }

        public synchronized void start() {
            if (running) return;
            running = true;
            worker = new Thread(this, "Pomodoro-Timer-Thread");
            worker.start();
        }

        public synchronized void pause() {
            running = false;
        }

        public synchronized void reset() {
            running = false;
            remainingSeconds = initialSeconds;
        }

        public synchronized boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (this) {
                    if (!running) return;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                synchronized (this) {
                    if (!running) return;
                    remainingSeconds--;
                    listener.onTick(remainingSeconds);

                    if (remainingSeconds <= 0) {
                        running = false;
                        listener.onFinished();
                        return;
                    }
                }
            }
        }
    }

    static class ProgressRing extends JPanel {
        private final int percent;
        private final Color color;

        ProgressRing(int percent, Color color) {
            this.percent = percent;
            this.color = color;
            setPreferredSize(new Dimension(220, 220));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight()) - 30;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;

            g2.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(PANEL_2);
            g2.drawOval(x, y, d, d);

            g2.setColor(color);
            g2.drawArc(x, y, d, d, 90, -(int)(360 * percent / 100.0));

            g2.setColor(WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 30));
            String s = percent + "%";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(s,
                    (getWidth() - fm.stringWidth(s)) / 2,
                    getHeight() / 2 + 10);

            g2.dispose();
        }
    }

    static class BarChart extends JPanel {
        BarChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(350, 180));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int base = getHeight() - 35;
            int left = 30;
            int chartHeight = Math.max(70, getHeight() - 65);

            int[] values = {5, 3, 8, 4, 7, 3, 5};
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            Color[] colors = {BLUE, PURPLE, MAGENTA, PINK, ORANGE, RED, CYAN};

            g2.setColor(new Color(70, 84, 135));
            g2.drawLine(left, base, getWidth() - 15, base);

            int barW = 25;
            int gap = 18;

            for (int i = 0; i < values.length; i++) {
                int h = values[i] * chartHeight / 10;
                int x = left + i * (barW + gap);

                g2.setPaint(new GradientPaint(
                        x, base - h, colors[i],
                        x, base, new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), 80)
                ));

                g2.fillRoundRect(x, base - h, barW, h, 8, 8);

                g2.setColor(MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.drawString(days[i], x + 2, base + 17);
            }

            g2.dispose();
        }
    }

    static class DonutChart extends JPanel {
        DonutChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 180));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(145, getHeight() - 20);
            int x = 18;
            int y = (getHeight() - d) / 2;

            int[] values = {5, 7, 3, 4, 5};
            Color[] colors = {GREEN, ORANGE, RED, BLUE, PURPLE};
            int total = 24;
            int start = 90;

            for (int i = 0; i < values.length; i++) {
                int angle = (int)Math.round(values[i] * 360.0 / total);
                g2.setColor(colors[i]);
                g2.setStroke(new BasicStroke(22));
                g2.drawArc(x + 11, y + 11, d - 22, d - 22, start, -angle);
                start -= angle;
            }

            g2.setColor(WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString("12", x + 52, y + 67);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(MUTED);
            g2.drawString("Tasks", x + 50, y + 82);

            int lx = d + 35;
            String[] names = {"Completed", "Pending", "High Priority", "Medium", "Low"};

            for (int i = 0; i < names.length; i++) {
                int yy = 35 + i * 25;
                g2.setColor(colors[i]);
                g2.fillOval(lx, yy, 9, 9);
                g2.setColor(MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(names[i], lx + 16, yy + 9);
            }

            g2.dispose();
        }
    }

    static class CalendarWidget extends JPanel {
        CalendarWidget() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String[] days = {"M", "T", "W", "T", "F", "S", "S"};
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));

            int cellW = Math.max(28, getWidth() / 7);
            int startY = 22;

            for (int i = 0; i < 7; i++) {
                g2.setColor(MUTED);
                g2.drawString(days[i], i * cellW + 8, startY);
            }

            LocalDate first = LocalDate.of(2026, 10, 1);
            int offset = first.getDayOfWeek().getValue() - 1;

            for (int day = 1; day <= 31; day++) {
                int index = offset + day - 1;
                int row = index / 7;
                int col = index % 7;

                int x = col * cellW + 2;
                int y = startY + 12 + row * 27;

                if (day == 8) {
                    g2.setColor(BLUE);
                    g2.fillOval(x, y - 12, 25, 25);
                    g2.setColor(WHITE);
                } else {
                    g2.setColor(MUTED);
                }

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(String.valueOf(day), x + 8, y + 5);
            }

            g2.dispose();
        }
    }

    static class LargeCalendar extends JPanel {
        LargeCalendar() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cellW = w / 7;
            int headerH = 45;
            int cellH = Math.max(55, (h - headerH) / 5);

            String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

            for (int c = 0; c < 7; c++) {
                g2.setColor(MUTED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(days[c], c * cellW + 12, 25);
            }

            LocalDate first = LocalDate.of(2026, 10, 1);
            int offset = first.getDayOfWeek().getValue() - 1;

            for (int day = 1; day <= 31; day++) {
                int idx = offset + day - 1;
                int row = idx / 7;
                int col = idx % 7;

                int x = col * cellW;
                int y = headerH + row * cellH;

                g2.setColor(new Color(50, 64, 115));
                g2.drawRect(x, y, cellW, cellH);

                if (day == 8) {
                    g2.setColor(new Color(60, 110, 255, 70));
                    g2.fillRect(x + 1, y + 1, cellW - 2, cellH - 2);
                }

                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(String.valueOf(day), x + 10, y + 20);
            }

            g2.dispose();
        }
    }


    // =========================================================
    // HERO PANEL - colorful premium dashboard banner
    // =========================================================

    static class HeroPanel extends JPanel {

        HeroPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            // Main blue -> purple gradient
            g2.setPaint(new GradientPaint(
                    0, 0,
                    new Color(31, 78, 190),
                    w, h,
                    new Color(119, 43, 196)
            ));

            g2.fillRoundRect(
                    0, 0,
                    w - 1,
                    h - 1,
                    28,
                    28
            );

            // Pink glow
            g2.setColor(
                    new Color(255, 52, 174, 80)
            );

            g2.fillOval(
                    w - 230,
                    -80,
                    300,
                    300
            );

            // Cyan glow
            g2.setColor(
                    new Color(0, 220, 255, 65)
            );

            g2.fillOval(
                    w - 80,
                    h - 105,
                    190,
                    190
            );

            // Decorative mountain layers
            Polygon back = new Polygon();

            back.addPoint(w - 420, h);
            back.addPoint(w - 330, h - 72);
            back.addPoint(w - 275, h - 30);
            back.addPoint(w - 205, h - 125);
            back.addPoint(w - 125, h - 45);
            back.addPoint(w - 40, h - 95);
            back.addPoint(w + 40, h);
            back.addPoint(w + 40, h + 20);
            back.addPoint(w - 420, h + 20);

            g2.setColor(
                    new Color(39, 39, 103, 150)
            );

            g2.fillPolygon(back);

            Polygon front = new Polygon();

            front.addPoint(w - 350, h);
            front.addPoint(w - 265, h - 55);
            front.addPoint(w - 205, h - 25);
            front.addPoint(w - 145, h - 100);
            front.addPoint(w - 75, h - 38);
            front.addPoint(w - 5, h - 80);
            front.addPoint(w + 50, h);
            front.addPoint(w + 50, h + 20);
            front.addPoint(w - 350, h + 20);

            g2.setColor(
                    new Color(20, 30, 85, 205)
            );

            g2.fillPolygon(front);

            // Border
            g2.setColor(
                    new Color(133, 164, 255, 125)
            );

            g2.setStroke(new BasicStroke(1.2f));

            g2.drawRoundRect(
                    0, 0,
                    w - 1,
                    h - 1,
                    28,
                    28
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    static class GlassPanelWithTitle extends JPanel {
        GlassPanelWithTitle(String title, JComponent content) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(12, 12, 12, 12));

            add(new JLabel(title) {{
                setForeground(WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 15));
                setBorder(new EmptyBorder(0, 5, 10, 0));
            }}, BorderLayout.NORTH);

            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(16, 25, 62, 240));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.setColor(new Color(72, 88, 150, 110));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            String enteredName = JOptionPane.showInputDialog(
                    null,
                    "Enter your name:",
                    "Welcome to Productivity Tool",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (enteredName == null || enteredName.trim().isEmpty()) {
                USER_NAME = "User";
            } else {
                USER_NAME = enteredName.trim();
            }

            new Main().setVisible(true);
        });
    }
}
