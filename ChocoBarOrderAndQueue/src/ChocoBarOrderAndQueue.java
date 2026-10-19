import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

// ==========================================
// 1. คลาส OrderItem: เก็บข้อมูลรายการอาหาร/เครื่องดื่มแต่ละชิ้น
// ==========================================
class OrderItem {
    private String name;
    private double price;

    public OrderItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (" + price + " บาท)";
    }
}

// ==========================================
// 2. คลาส ChocoMenuNode: โครงสร้างข้อมูลแบบ Tree (Menu Category Tree)
// ใช้สำหรับจัดการหมวดหมู่เมนูอาหารและแสดงผลแบบลำดับชั้น (Tree Traversal)
// ==========================================
class ChocoMenuNode {
    private String name;
    private double price;
    private boolean isCategory; // true = เป็นหมวดหมู่ (Parent/Internal Node), false = เป็นสินค้า (Leaf Node)
    private List<ChocoMenuNode> children = new ArrayList<>(); // เก็บกิ่งก้านสาขาของ Tree

    // Constructor สำหรับสร้างหมวดหมู่ (Category Node)
    public ChocoMenuNode(String name) {
        this.name = name;
        this.isCategory = true;
    }

    // Constructor สำหรับสร้างรายการสินค้า (Leaf Node)
    public ChocoMenuNode(String name, double price) {
        this.name = name;
        this.price = price;
        this.isCategory = false;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    public void addChild(ChocoMenuNode child) {
        children.add(child);
    }

    public List<ChocoMenuNode> getChildren() {
        return children;
    }

    // ฟังก์ชัน Recursive สำหรับแสดงผลโครงสร้าง Tree แบบลำดับชั้น (Pre-order Traversal)
    public void displayTree(String indent) {
        System.out.println(indent
                + (isCategory ? "[Category] " : "   [Item] ")
                + name
                + (isCategory ? "" : " (" + price + " บาท)"));

        for (ChocoMenuNode child : children) {
            child.displayTree(indent + "    ");
        }
    }
}

// ==========================================
// 3. คลาส KitchenOrder: โครงสร้างข้อมูลแบบ Queue (FIFO)
// ใช้สำหรับรวมชุดบิลที่ลูกค้ายืนยันแล้ว ส่งต่อให้ห้องครัวทำงานตามลำดับคิว
// ==========================================
class KitchenOrder {
    private static int idCounter = 1;
    private int orderId = idCounter++;

    // ภายในออเดอร์ครัว จะนำ Stack ของบิลมาเก็บไว้เพื่อรอเชฟทำอาหาร
    private Stack<OrderItem> items = new Stack<>();

    public KitchenOrder(Stack<OrderItem> billItems) {
        items.addAll(billItems);
    }

    public int getOrderId() { return orderId; }

    // ฟังก์ชันพิมพ์รายละเอียดออเดอร์ในครัว พร้อมคำนวณราคารวม
    public void printDetails() {
        System.out.println("--- ออเดอร์ #" + orderId + " ---");
        double total = 0;
        for (OrderItem item : items) {
            System.out.println(" - " + item);
            total += item.getPrice();
        }
        System.out.println("ราคารวม: " + total + " บาท");
    }
}

// ==========================================
// 4. คลาสหลัก ChocoBarOrderAndQueue: ควบคุมการทำงานของระบบทั้งหมด
// ==========================================
public class ChocoBarOrderAndQueue {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- ส่วนที่สร้าง Tree โครงสร้างเมนูร้าน ChocoBar ---
        ChocoMenuNode root = new ChocoMenuNode("Menu Root");

        // หมวดหมู่ที่ 1: ขนมหวาน (Sweets & Desserts)
        ChocoMenuNode desserts = new ChocoMenuNode("Sweets & Desserts");
        desserts.addChild(new ChocoMenuNode("Choco Lava Cake", 129.0));
        desserts.addChild(new ChocoMenuNode("Brownie Sundae", 99.0));
        desserts.addChild(new ChocoMenuNode("Cupcake", 49.0));
        desserts.addChild(new ChocoMenuNode("Strawberry Cheesecake", 100.0));

        // หมวดหมู่ที่ 2: เครื่องดื่ม (Drinks)
        ChocoMenuNode drinks = new ChocoMenuNode("Drinks");
        drinks.addChild(new ChocoMenuNode("Dark Choco", 85.0));
        drinks.addChild(new ChocoMenuNode("Matcha Latte", 89.0));
        drinks.addChild(new ChocoMenuNode("Coconut Matcha Latte", 95.0));
        drinks.addChild(new ChocoMenuNode("Latte", 80.0));
        drinks.addChild(new ChocoMenuNode("Dark Coffee", 70.0));

        // ผูกหมวดหมู่เข้ากับ Root ของ Tree
        root.addChild(desserts);
        root.addChild(drinks);

        // --- โครงสร้างข้อมูล Stack และ Queue สำหรับจัดการบิลและคิว ---
        Stack<OrderItem> currentBill = new Stack<>(); // Stack สำหรับบิลปัจจุบัน (หลักการ LIFO: เข้าทีหลัง ออกก่อน)
        Queue<KitchenOrder> kitchenQueue = new LinkedList<>(); // Queue สำหรับคิวครัว (หลักการ FIFO: เข้าก่อน ออกก่อน)

        int choice = -1;

        // วนลูปแสดงเมนูหลักของโปรแกรม (Menu-driven)
        while (choice != 0) {
            System.out.println("\n=== ChocoBar Order & Queue ===");
            System.out.println("1. แสดงเมนู (Tree)");
            System.out.println("2. สั่งอาหาร (Push)");
            System.out.println("3. ยกเลิกรายการล่าสุด (Pop)");
            System.out.println("4. ส่งเข้าครัว (Enqueue)");
            System.out.println("5. ครัวทำเสร็จ (Dequeue)");
            System.out.println("6. แสดงบิลและคิว");
            System.out.println("0. ออกจากระบบ");
            System.out.print("เลือก (0-6): ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // เรียกใช้ฟังก์ชัน displayTree ท่องโครงสร้าง Tree เพื่อแสดงเมนูทั้งหมดแบบลำดับชั้น
                    root.displayTree("");
                    break;

                case 2:
                    // ดึงรายการอาหารจาก Tree มาแปลงเป็นลิสต์เพื่อให้ลูกคือกดเลือกสั่ง (Push ลง Stack)
                    List<ChocoMenuNode> flatList = new ArrayList<>();
                    int idx = 1;
                    for (ChocoMenuNode cat : root.getChildren()) {
                        System.out.println("[" + cat.getName() + "]");
                        for (ChocoMenuNode item : cat.getChildren()) {
                            System.out.println("  " + idx + ". " + item.getName() + " (" + item.getPrice() + " บาท)");
                            flatList.add(item);
                            idx++;
                        }
                    }
                    System.out.print("เลือกเบอร์เมนู (0 ยกเลิก): ");
                    int mChoice = scanner.nextInt();
                    scanner.nextLine();
                    if (mChoice > 0 && mChoice <= flatList.size()) {
                        ChocoMenuNode selected = flatList.get(mChoice - 1);
                        // ใช้คำสั่ง push() เพิ่มรายการลงในบิลปัจจุบัน (Stack) ตามหลัก LIFO
                        currentBill.push(new OrderItem(selected.getName(), selected.getPrice()));
                        System.out.println("-> เพิ่ม [" + selected.getName() + "] ลงบิลแล้ว");
                    }
                    break;

                case 3:
                    // ใช้คำสั่ง pop() ยกเลิกรายการอาหารล่าสุดที่เพิ่งสั่งไป (Stack - LIFO)
                    if (!currentBill.empty()) {
                        System.out.println("-> ยกเลิก [" + currentBill.pop().getName() + "]");
                    } else {
                        System.out.println("-> บิลว่างเปล่า");
                    }
                    break;

                case 4:
                    // นำบิลปัจจุบันใน Stack มารวมเป็นออเดอร์ แล้วส่งเข้าคิวครัวด้วย offer() (Queue - Enqueue / FIFO)
                    if (!currentBill.empty()) {
                        KitchenOrder order = new KitchenOrder(currentBill);
                        kitchenQueue.offer(order);
                        System.out.println("-> ส่งออเดอร์ #" + order.getOrderId() + " เข้าครัวแล้ว");
                        currentBill.clear(); // ล้างบิลชั่วคราวเพื่อรับบิลถัดไป
                    } else {
                        System.out.println("-> บิลว่างเปล่า");
                    }
                    break;

                case 5:
                    // ใช้คำสั่ง poll() ดึงออเดอร์แรกสุดออกจากคิวครัวเมื่อทำเสร็จ (Queue - Dequeue / FIFO)
                    if (!kitchenQueue.isEmpty()) {
                        kitchenQueue.poll().printDetails();
                    } else {
                        System.out.println("-> ไม่มีออเดอร์ในครัว");
                    }
                    break;

                case 6:
                    // แสดงสถานะบิลปัจจุบัน (Stack) และคิวที่กำลังรอในห้องครัว (Queue)
                    System.out.println("\n[บิลปัจจุบัน Stack]");
                    if (currentBill.empty()) {
                        System.out.println("(ว่าง)");
                    } else {
                        for (int i = currentBill.size() - 1; i >= 0; i--) {
                            System.out.println(" - " + currentBill.get(i));
                        }
                    }

                    System.out.println("\n[คิวครัว Queue]");
                    if (kitchenQueue.isEmpty()) {
                        System.out.println("(ว่าง)");
                    } else {
                        int qIdx = 1;
                        for (KitchenOrder q : kitchenQueue) {
                            System.out.print("คิวที่ " + qIdx++ + " ");
                            q.printDetails();
                        }
                    }
                    break;

                case 0:
                    System.out.println("จบการทำงาน");
                    break;
            }
        }
        scanner.close();
    }
}