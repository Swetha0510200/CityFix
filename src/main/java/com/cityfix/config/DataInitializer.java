package com.cityfix.config;

import com.cityfix.entity.Admin;
import com.cityfix.entity.Complaint;
import com.cityfix.entity.User;
import com.cityfix.repository.AdminRepository;
import com.cityfix.repository.ComplaintRepository;
import com.cityfix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize Default Admin
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
            System.out.println(">>> [CityFix] Default Admin initialized: Username=admin / Password=admin123");
        }

        // 2. Initialize Demo Citizen and Sample Complaints for Instant Demo / Viva
        if (userRepository.count() == 0) {
            User citizen1 = new User();
            citizen1.setName("Rahul Sharma");
            citizen1.setEmail("citizen@cityfix.com");
            citizen1.setPhone("+91 98765 43210");
            citizen1.setPasswordHash(passwordEncoder.encode("citizen123"));
            citizen1.setRole("CITIZEN");
            userRepository.save(citizen1);

            User citizen2 = new User();
            citizen2.setName("Priya Patel");
            citizen2.setEmail("priya@cityfix.com");
            citizen2.setPhone("+91 91234 56789");
            citizen2.setPasswordHash(passwordEncoder.encode("priya123"));
            citizen2.setRole("CITIZEN");
            userRepository.save(citizen2);

            System.out.println(">>> [CityFix] Demo Citizens initialized: citizen@cityfix.com / citizen123");

            // Seed initial sample complaints
            if (complaintRepository.count() == 0) {
                Complaint c1 = new Complaint();
                c1.setComplaintId("CF-2026-0001");
                c1.setUser(citizen1);
                c1.setTitle("Deep pothole causing traffic jams");
                c1.setCategory("Pothole");
                c1.setDescription("Large pothole near Sector 14 main road intersection causing severe traffic bottlenecks and bike skidding risk.");
                c1.setLocation("Sector 14 Main Crossroad, Near Metro Gate 2");
                c1.setLatitude(28.6139);
                c1.setLongitude(77.2090);
                c1.setPriority("High");
                c1.setStatus("In Progress");
                c1.setAdminRemarks("Work order assigned to Road Maintenance Team #4. Repair scheduled for tonight.");
                complaintRepository.save(c1);

                Complaint c2 = new Complaint();
                c2.setComplaintId("CF-2026-0002");
                c2.setUser(citizen1);
                c2.setTitle("Non-functional streetlights on 5th Avenue");
                c2.setCategory("Streetlight");
                c2.setDescription("Five consecutive street poles are dark for the last 3 days making the stretch unsafe for pedestrians after 7 PM.");
                c2.setLocation("5th Avenue, Ward 12, Behind City Park");
                c2.setLatitude(28.6150);
                c2.setLongitude(77.2105);
                c2.setPriority("Medium");
                c2.setStatus("Resolved");
                c2.setAdminRemarks("Electrical team replaced faulty junction box and 5 LED fixtures. All lights tested functional.");
                complaintRepository.save(c2);

                Complaint c3 = new Complaint();
                c3.setComplaintId("CF-2026-0003");
                c3.setUser(citizen2);
                c3.setTitle("Overflowing garbage bin near residential colony");
                c3.setCategory("Garbage");
                c3.setDescription("Community garbage container has not been cleared for over 4 days, causing foul smell and health hazard.");
                c3.setLocation("Green Valley Colony, Gate #3");
                c3.setLatitude(28.6180);
                c3.setLongitude(77.2150);
                c3.setPriority("High");
                c3.setStatus("Assigned");
                c3.setAdminRemarks("Sanitation supervisor notified. Compactor truck dispatched.");
                complaintRepository.save(c3);

                Complaint c4 = new Complaint();
                c4.setComplaintId("CF-2026-0004");
                c4.setUser(citizen2);
                c4.setTitle("Major water pipeline leakage");
                c4.setCategory("Water Leakage");
                c4.setDescription("Clean potable water is gushing out of an underground pipeline crack onto the street sidewalk.");
                c4.setLocation("Market Road, Opposite National Bank");
                c4.setLatitude(28.6200);
                c4.setLongitude(77.2180);
                c4.setPriority("High");
                c4.setStatus("Pending");
                c4.setAdminRemarks(null);
                complaintRepository.save(c4);

                System.out.println(">>> [CityFix] Sample complaints initialized.");
            }
        }
    }
}
