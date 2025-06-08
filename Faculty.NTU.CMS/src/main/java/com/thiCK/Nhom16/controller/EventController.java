package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Event;
import com.thiCK.Nhom16.repository.EventRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventRepository eventRepo;
    private final ActivityService activityService;

    @Autowired
    public EventController(EventRepository eventRepo,
                           ActivityService activityService) {
        this.eventRepo = eventRepo;
        this.activityService = activityService;
    }

    // 🗓️ Hiển thị trang danh sách sự kiện với layout người dùng
    @GetMapping("/all")
    public String listEvents(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "fromDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(name = "toDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        Model model) {

        List<Event> filtered = eventRepo.findAll().stream()
            .filter(e -> keyword == null || keyword.isBlank()
                    || e.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .filter(e -> status == null || status.isBlank()
                    || e.getStatus().equalsIgnoreCase(status))
            .filter(e -> fromDate == null
                    || !e.getStartDate().isBefore(fromDate))
            .filter(e -> toDate == null
                    || !e.getStartDate().isAfter(toDate))
            .collect(Collectors.toList());

        model.addAttribute("events", filtered);
        model.addAttribute("page", "events"); // để active menu
        model.addAttribute("pageTitle", "Sự kiện");

        // ✅ Trả về đúng template mới
        return "event/event_list";
    }

    // 📊 API trả JSON cho FullCalendar
    @GetMapping("/api")
    @ResponseBody
    public List<Map<String, Object>> getEventData(
        @RequestParam(name = "keyword", required = false) String keyword) {

        return eventRepo.findAll().stream()
            .filter(e -> keyword == null || keyword.isBlank()
                    || e.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", e.getId());
                m.put("title", e.getTitle());
                m.put("start", e.getStartDate().toString());
                m.put("end", e.getEndDate() != null ? e.getEndDate().toString() : null);
                m.put("startTime", e.getStartTime() != null ? e.getStartTime().toString() : null);
                m.put("endTime", e.getEndTime() != null ? e.getEndTime().toString() : null);
                m.put("location", e.getLocation());
                m.put("organizer", e.getOrganizer());
                m.put("status", e.getStatus());
                return m;
            })
            .collect(Collectors.toList());
    }

    // ➕ Form tạo mới sự kiện
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "event/event_form";
    }

    // 💾 Lưu sự kiện
    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") Event event) {
        boolean isNew = (event.getId() == null);
        Event saved = eventRepo.save(event);

        if (isNew) {
            activityService.log("Created event '" + saved.getTitle() + "'",
                    "/event/view/" + saved.getId(), "calendar-event");
        } else {
            activityService.log("Updated event '" + saved.getTitle() + "'",
                    "/event/view/" + saved.getId(), "pencil-square");
        }

        return "redirect:/event/all";
    }

    // 👁️ Xem chi tiết
    @GetMapping("/view/{id}")
    public String viewEvent(@PathVariable("id") Long id, Model model) {
        Event event = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        model.addAttribute("event", event);
        return "event/event_view";
    }

    // ✏️ Chỉnh sửa
    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable("id") Long id, Model model) {
        Event event = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        model.addAttribute("event", event);
        return "event/event_form";
    }

    // ❌ Xóa
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id) {
        Event e = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        eventRepo.deleteById(id);

        activityService.log("Deleted event '" + e.getTitle() + "'",
                "/event/all", "calendar-event");

        return "redirect:/event/all";
    }
    @GetMapping("/su-kien")
    public String showEventPublic(Model model) {
        List<Event> events = eventRepo.findAll();

        // Gắn status để dùng cho lọc giao diện
        for (Event e : events) {
            if ("Scheduled".equalsIgnoreCase(e.getStatus())) {
                e.setStatus("upcoming");
            } else if ("Completed".equalsIgnoreCase(e.getStatus())) {
                e.setStatus("past");
            } else {
                e.setStatus("ongoing");
            }
        }

        model.addAttribute("events", events);
        model.addAttribute("page", "events");
        model.addAttribute("pageTitle", "Sự kiện");
        return "user/event/event";
    }


}
