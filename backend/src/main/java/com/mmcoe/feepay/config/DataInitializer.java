package com.mmcoe.feepay.config;

import com.mmcoe.feepay.admin.AuditLog;
import com.mmcoe.feepay.admin.AuditLogRepository;
import com.mmcoe.feepay.admin.SecurityLog;
import com.mmcoe.feepay.admin.SecurityLogRepository;
import com.mmcoe.feepay.auth.Role;
import com.mmcoe.feepay.auth.RoleRepository;
import com.mmcoe.feepay.auth.UserAccount;
import com.mmcoe.feepay.auth.UserRepository;
import com.mmcoe.feepay.feestructure.FeeStructure;
import com.mmcoe.feepay.feestructure.FeeStructureRepository;
import com.mmcoe.feepay.installment.FeeInstallment;
import com.mmcoe.feepay.installment.InstallmentRepository;
import com.mmcoe.feepay.payment.FeePayment;
import com.mmcoe.feepay.payment.PaymentRepository;
import com.mmcoe.feepay.payment.Receipt;
import com.mmcoe.feepay.payment.ReceiptRepository;
import com.mmcoe.feepay.payment.Transaction;
import com.mmcoe.feepay.payment.TransactionRepository;
import com.mmcoe.feepay.student.FeeAssignment;
import com.mmcoe.feepay.student.FeeAssignmentRepository;
import com.mmcoe.feepay.student.FeeAssignmentService;
import com.mmcoe.feepay.student.Student;
import com.mmcoe.feepay.student.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Ensures baseline MMCOE seed data exists upon application startup.
 * Aligns with Section 7 (Predefined Category Fee Structure), Section 9 (Realistic Demo Students),
 * and Section 26 (Review Demonstration Flow).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final FeeAssignmentRepository feeAssignmentRepository;
    private final FeeAssignmentService feeAssignmentService;
    private final InstallmentRepository installmentRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final ReceiptRepository receiptRepository;
    private final AuditLogRepository auditLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            FeeStructureRepository feeStructureRepository,
            FeeAssignmentRepository feeAssignmentRepository,
            FeeAssignmentService feeAssignmentService,
            InstallmentRepository installmentRepository,
            PaymentRepository paymentRepository,
            TransactionRepository transactionRepository,
            ReceiptRepository receiptRepository,
            AuditLogRepository auditLogRepository,
            SecurityLogRepository securityLogRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.feeAssignmentRepository = feeAssignmentRepository;
        this.feeAssignmentService = feeAssignmentService;
        this.installmentRepository = installmentRepository;
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.receiptRepository = receiptRepository;
        this.auditLogRepository = auditLogRepository;
        this.securityLogRepository = securityLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // If demo student already exists, skip re-seeding
        if (studentRepository.findByPrn("B25IT2009").isPresent()) {
            System.out.println("[INFO] Database already contains initialized MMCOE demo seed data.");
            return;
        }

        System.out.println("[INFO] Bootstrapping MMCOE Fee Payment Management Platform seed data...");

        // 1. Roles
        Role roleStudent = roleRepository.findByRoleName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT")));
        Role roleOfficer = roleRepository.findByRoleName("ROLE_ACCOUNTS_OFFICER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ACCOUNTS_OFFICER")));
        Role roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // 2. Predefined Category-Based Fee Structures (Section 7)
        // Academic Year 2026-2027, Semester 6, Information Technology
        LocalDate dueDate = LocalDate.of(2026, 11, 30);

        // 1. OPEN - Total ₹1,45,500
        FeeStructure fsOpen = feeStructureRepository.save(new FeeStructure(
                "Information Technology", 6, "OPEN", "TUITION_DEVELOPMENT_COMPOSITE",
                new BigDecimal("120000.00"), new BigDecimal("20000.00"), new BigDecimal("5000.00"), new BigDecimal("500.00"),
                dueDate, "2026-2027", "Standard Open Category Composite Academic Fee"
        ));

        // 2. OBC/EBC/EWS/SEBC Male - Total ₹86,587
        FeeStructure fsObcMale = feeStructureRepository.save(new FeeStructure(
                "Information Technology", 6, "OBC/EBC/EWS/SEBC Male", "TUITION_DEVELOPMENT_COMPOSITE",
                new BigDecimal("60000.00"), new BigDecimal("20000.00"), new BigDecimal("6087.00"), new BigDecimal("500.00"),
                dueDate, "2026-2027", "OBC/EBC/EWS/SEBC Male Concessional Academic Fee"
        ));

        // 3. SC/ST - Total ₹10,000
        FeeStructure fsScSt = feeStructureRepository.save(new FeeStructure(
                "Information Technology", 6, "SC/ST", "TUITION_DEVELOPMENT_COMPOSITE",
                new BigDecimal("0.00"), new BigDecimal("5000.00"), new BigDecimal("4500.00"), new BigDecimal("500.00"),
                dueDate, "2026-2027", "SC/ST Category Government Scholarship Supported Fee"
        ));

        // 4. VJNT/SBC/TFWS - Total ₹27,674
        FeeStructure fsVjnt = feeStructureRepository.save(new FeeStructure(
                "Information Technology", 6, "VJNT/SBC/TFWS", "TUITION_DEVELOPMENT_COMPOSITE",
                new BigDecimal("0.00"), new BigDecimal("20000.00"), new BigDecimal("7174.00"), new BigDecimal("500.00"),
                dueDate, "2026-2027", "VJNT / SBC / TFWS Concessional Academic Fee"
        ));

        // 5. J & K Quota - Total ₹34,000
        FeeStructure fsJk = feeStructureRepository.save(new FeeStructure(
                "Information Technology", 6, "J & K Quota", "TUITION_DEVELOPMENT_COMPOSITE",
                new BigDecimal("20000.00"), new BigDecimal("10000.00"), new BigDecimal("3500.00"), new BigDecimal("500.00"),
                dueDate, "2026-2027", "Jammu & Kashmir Quota Special Academic Fee"
        ));

        // 6. OBC/EBC/EWS/SEBC Female - Total ₹27,674 (Target Category for demo student Saburi Yeola)
        FeeStructure fsObcFemale = feeStructureRepository.save(new FeeStructure(
                "Information Technology", 6, "OBC/EBC/EWS/SEBC Female", "TUITION_DEVELOPMENT_COMPOSITE",
                new BigDecimal("0.00"), new BigDecimal("20000.00"), new BigDecimal("7174.00"), new BigDecimal("500.00"),
                dueDate, "2026-2027", "OBC/EBC/EWS/SEBC Female Full Tuition Waiver Composite Fee"
        ));

        // 3. Realistic Demo Students across different categories (Section 9)
        // Primary Demo Student: Saburi Yeola
        Student studentSaburi = studentRepository.save(new Student(
                "B25IT2009", "SABURI YEOLA", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology",
                6, "2026-2027", "OBC/EBC/EWS/SEBC Female", "saburi.yeola@mmcoe.edu.in", "9876543210"
        ));

        Student s1 = studentRepository.save(new Student("B25IT2001", "Aarav Sharma", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "OPEN", "aarav.sharma@mmcoe.edu.in", "9876543211"));
        Student s2 = studentRepository.save(new Student("B25IT2002", "Priya Patel", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "OBC/EBC/EWS/SEBC Female", "priya.patel@mmcoe.edu.in", "9876543212"));
        Student s3 = studentRepository.save(new Student("B25IT2003", "Rohan Deshmukh", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "OBC/EBC/EWS/SEBC Male", "rohan.deshmukh@mmcoe.edu.in", "9876543213"));
        Student s4 = studentRepository.save(new Student("B25IT2004", "Sneha Kamble", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "SC/ST", "sneha.kamble@mmcoe.edu.in", "9876543214"));
        Student s5 = studentRepository.save(new Student("B25IT2005", "Tanmay Jadhav", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "VJNT/SBC/TFWS", "tanmay.jadhav@mmcoe.edu.in", "9876543215"));
        Student s6 = studentRepository.save(new Student("B25IT2006", "Aarav Lone", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "J & K Quota", "aarav.lone@mmcoe.edu.in", "9876543216"));
        Student s7 = studentRepository.save(new Student("B25IT2007", "Aditya Joshi", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "OPEN", "aditya.joshi@mmcoe.edu.in", "9876543217"));
        Student s8 = studentRepository.save(new Student("B25IT2008", "Shweta Kulkarni", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "OBC/EBC/EWS/SEBC Female", "shweta.kulkarni@mmcoe.edu.in", "9876543218"));
        Student s10 = studentRepository.save(new Student("B25IT2010", "Vaibhav Patil", "B.TECH. INFORMATION TECHNOLOGY", "Information Technology", 6, "2026-2027", "OBC/EBC/EWS/SEBC Male", "vaibhav.patil@mmcoe.edu.in", "9876543219"));

        // 4. User Accounts (Hashed passwords with BCrypt)
        String encodedPwd = passwordEncoder.encode("password123");

        // Student Account 1 (Login with PRN B25IT2009 or student1)
        userRepository.save(new UserAccount("B25IT2009", encodedPwd, roleStudent, studentSaburi));
        userRepository.save(new UserAccount("student1", encodedPwd, roleStudent, studentSaburi));

        // Accounts Officer Account
        userRepository.save(new UserAccount("officer1", encodedPwd, roleOfficer, null));

        // Admin Account
        userRepository.save(new UserAccount("admin1", encodedPwd, roleAdmin, null));

        // 5. Automated Fee Assignment (Section 7 & 36)
        // Saburi Yeola (OBC/EBC/EWS/SEBC Female) -> automatically assigned ₹27,674 (UNPAID/PENDING)
        feeAssignmentService.assignFeeToStudent(studentSaburi);

        // Assign fees to all other seeded students based on their respective categories
        List.of(s1, s2, s3, s4, s5, s6, s7, s8, s10).forEach(feeAssignmentService::assignFeeToStudent);

        // 6. Demonstrate Installment flow for review with Priya Patel (s2)
        List<FeeAssignment> priyaAssignments = feeAssignmentRepository.findByStudent_StudentId(s2.getStudentId());
        if (!priyaAssignments.isEmpty()) {
            FeeAssignment assignPriya = priyaAssignments.get(0);
            assignPriya.setStatus("INSTALLMENT_APPROVED");
            assignPriya.applyPayment(new BigDecimal("13837.00")); // Paid first installment
            feeAssignmentRepository.save(assignPriya);

            // 2 Installment records (₹13,837 each)
            FeeInstallment inst1 = installmentRepository.save(new FeeInstallment(assignPriya, 1, LocalDate.of(2026, 9, 30), new BigDecimal("13837.00")));
            inst1.markPaid();
            installmentRepository.save(inst1);

            FeeInstallment inst2 = installmentRepository.save(new FeeInstallment(assignPriya, 2, LocalDate.of(2026, 11, 30), new BigDecimal("13837.00")));

            // Demo previous payment record for Priya
            FeePayment paymentPriya = paymentRepository.save(new FeePayment(inst1, s2, new BigDecimal("13837.00"), "ONLINE_NETBANKING", "SUCCESS"));
            Transaction txPriya = transactionRepository.save(new Transaction(paymentPriya, "SUCCESS", "MMCOE-TXN-PREV-2026-001"));
            receiptRepository.save(new Receipt(txPriya, "MMCOE-2026-RCP-0001"));
        }

        // 7. Audit & Security Logs Baseline
        auditLogRepository.save(new AuditLog(null, "INITIALIZED_MMCOE_DATABASE", "Initialized 6 category fee structures & 10 students", "127.0.0.1"));
        auditLogRepository.save(new AuditLog(null, "FEE_ASSIGNMENT_CREATED", "Automated fee assignment for PRN: B25IT2009 (₹27,674)", "127.0.0.1"));
        securityLogRepository.save(new SecurityLog(null, "SYSTEM_STARTUP", "127.0.0.1"));

        System.out.println("[INFO] MMCOE Fee Payment Platform seed data successfully initialized!");
        System.out.println("       Demo Accounts:");
        System.out.println("       - Student: PRN B25IT2009 (or student1) / password123 (Saburi Yeola, Fee: ₹27,674)");
        System.out.println("       - Accounts Officer: officer1 / password123");
        System.out.println("       - Admin: admin1 / password123");
    }
}
