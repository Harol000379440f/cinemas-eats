package co.bebolder.cinemaseats;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeatsService {

    private static final String FILE_PATH = System.getenv("PATH_FILE");


    public Map<String, Boolean> getSeats() {
        Map<String, Boolean> seats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    seats.put(parts[0], Boolean.parseBoolean(parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seats;
    }

    public void updateSeat(String seatNumber, boolean availability) {
        Map<String, Boolean> seats = getSeats();
        if (seats.containsKey(seatNumber)) {
            seats.put(seatNumber, availability);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Map.Entry<String, Boolean> entry : seats.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
