package org.example.projectspringfalling.playlist;

import org.example.projectspringfalling.RestAPI.RestResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    // 보관함 - 내 리스트
    // 플레이리스트 모달
    @Query("SELECT new org.example.projectspringfalling.RestAPI.RestResponse$StoragePlaylist(p.id, p.name, MIN(a.albumImg), COUNT(ps), p.createdAt) " +
            "FROM Playlist p " +
            "JOIN p.playlistSong ps " +
            "JOIN ps.song s " +
            "JOIN s.album a " +
            "WHERE p.user.id = :userId " +
            "GROUP BY p.id, p.name, p.createdAt")
    List<RestResponse.StoragePlaylist> findAllPlaylistById(Integer userId);

}
