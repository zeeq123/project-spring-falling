package org.example.projectspringfalling.song;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SongService {
    private final SongRepository songRepository;
}
