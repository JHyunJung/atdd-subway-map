package subway.line;

import subway.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 15, nullable = false)
    private String color;

    @Embedded
    private Stations stations = new Stations();

    private int distance;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        addStation(upStation);
        addStation(downStation);
        this.distance = distance;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public void removeStation(Station station){
        stations.remove(station);
    }

    public List<Station> getStations() {
        return stations.getStations();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }
}
