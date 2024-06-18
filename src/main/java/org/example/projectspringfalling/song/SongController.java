package org.example.projectspringfalling.song;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SongController {

    @GetMapping("/songs/{id}")
    public String songDetail(@PathVariable Integer id) {
        return "song/song-detail";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/playlists/{id}")
    public String playlist(@PathVariable Integer id) {
        return "song/playlist";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }



    @GetMapping("/song-chart")
    public String songChart() {
        return "song/song-chart";
    }

    @GetMapping("/song-genre")
    public String songGenre(){
        return "song/song-genre";
    }

}
