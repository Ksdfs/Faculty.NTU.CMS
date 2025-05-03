package com.thiCK.Nhom16.controller;

import com.thiCK.Nhom16.entity.Event;
import com.thiCK.Nhom16.repository.EventRepository;
import com.thiCK.Nhom16.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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

    // 🗓️ Hiển thị trang calendar/list, có filter
    @GetMapping("/all")
    public String listEvents(
        @RequestParam(name="keyword",  required=false) String keyword,
        @RequestParam(name="status",   required=false) String status,
        @RequestParam(name="fromDate", required=false)
          @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
        @RequestParam(name="toDate",   required=false)
          @DateTimeFormat(iso = ISO.DATE) LocalDate toDate,
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

        model.addAttribute("events",   filtered);
        model.addAttribute("keyword",  keyword);
        model.addAttribute("status",   status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate",   toDate);
        return "event/event_list";
    }

    // 📊 API trả JSON cho FullCalendar
    @GetMapping("/api")
    @ResponseBody
    public List<Map<String,Object>> getEventData(
            @RequestParam(name="keyword", required=false) String keyword) {

        return eventRepo.findAll().stream()
            .filter(e -> keyword == null
                       || keyword.isBlank()
                       || e.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",         e.getId());
                m.put("title",      e.getTitle());
                m.put("startDate",  e.getStartDate().toString());
                m.put("startTime",  e.getStartTime().toString());
                m.put("endTime",    e.getEndTime().toString());
                m.put("location",   e.getLocation());
                m.put("organizer",  e.getOrganizer());
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

    // 💾 Lưu sự kiện (tạo mới hoặc cập nhật)
    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") Event event) {
        boolean isNew = (event.getId() == null);
        Event saved = eventRepo.save(event);

        if (isNew) {
            activityService.log(
                "Created event '" + saved.getTitle() + "'",
                "/event/view/" + saved.getId(),
                "calendar-event"          // icon tạo mới
            );
        } else {
            activityService.log(
                "Updated event '" + saved.getTitle() + "'",
                "/event/view/" + saved.getId(),
                "pencil-square"    // icon cập nhật
            );
        }

        return "redirect:/event/all";
    }

    // 👁️ Xem chi tiết sự kiện
    @GetMapping("/view/{id}")
    public String viewEvent(@PathVariable("id") Long id, Model model) {
        Event event = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        model.addAttribute("event", event);
        return "event/event_view";
    }

    // ✏️ Form chỉnh sửa sự kiện
    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable("id") Long id, Model model) {
        Event event = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        model.addAttribute("event", event);
        return "event/event_form";
    }

    // ❌ Xóa sự kiện
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable("id") Long id) {
        Event e = eventRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        eventRepo.deleteById(id);

        activityService.log(
            "Deleted event '" + e.getTitle() + "'",
            "/event/all",
            "calendar-event"             // icon xóa
        );

        return "redirect:/event/all";
    }
}
