package co.bebolder.cinemaseats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/seats")
public class SeatsController {

    @Autowired
    private SeatsService seatService;


    @CrossOrigin(origins = "*")
    @GetMapping("/available")
    public Map<String, Boolean> getAvailableSeats() {
        return seatService.getSeats();
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/book/{seatNumber}")
    public ResponseEntity<Map<String, String>> bookSeat(@PathVariable String seatNumber) {
        seatService.updateSeat(seatNumber,false);
        Map<String, String> response = new HashMap<>();
        response.put("message", seatNumber + " reservada exitosamente.");
        return ResponseEntity.status(200).body(response);
    }
}
