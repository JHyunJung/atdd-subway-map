package subway.line;

import subway.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Stations {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Station> stations = new ArrayList<>();

    public Stations() {

    }

    public List<Station> getStations(){
        return stations;
    }

    public void add(Station station) {
        stations.add(station);
    }

    public void remove(Station station) {
        stations.remove(station);
    }




}
