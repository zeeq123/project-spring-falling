package org.example.projectspringfalling.song;

import org.example.projectspringfalling.admin.AdminResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {

    // 해외 팝 차트
    @Query("SELECT s FROM Song s JOIN FETCH s.album JOIN FETCH s.artist WHERE s.album.nationality='해외' AND s.genre='Pop' ORDER BY s.listenCount DESC")
    List<Song> findGlobalPopChart();

    // 국내 발라드 차트
    @Query("SELECT s FROM Song s JOIN FETCH s.album JOIN FETCH s.artist WHERE s.album.nationality='국내' AND s.genre='Ballad' ORDER BY s.listenCount DESC")
    List<Song> findDomesticBalladChart();

    // 해외 소셜 차트
    @Query("SELECT s FROM Song s JOIN FETCH s.album JOIN FETCH s.artist WHERE s.album.nationality='해외' ORDER BY s.listenCount DESC")
    List<Song> findGlobalChart();

    // 메인 차트
    @Query("SELECT s FROM Song s JOIN FETCH s.album JOIN FETCH s.artist ORDER BY s.listenCount DESC")
    List<Song> findMainChart();

    // Song id로 song, album 찾기
    @Query("SELECT s, al FROM Song s JOIN FETCH s.album al WHERE s.id=:songId")
    Song findSongAndAlbumById(@Param("songId") Integer songId);

    // Song id로 song, album, artist 찾기
    @Query("SELECT s, al, ar FROM Song s JOIN FETCH Album al ON al.id=s.album.id JOIN FETCH Artist ar on ar.id=al.artist.id WHERE al.artist.id=ar.id AND s.id=:songId")
    Song findSongAndAlbumAndArtistById(@Param("songId") Integer songId);

    // Album id로 song 찾기
    @Query("SELECT s, ar FROM Song s JOIN FETCH Artist ar ON ar.id=s.artist.id WHERE s.album.id=:AlbumId")
    List<Song> findByAlbumId(@Param("AlbumId") Integer AlbumId);

    // 검색
    @Query("select s,ar,al from Song s join fetch s.album al join fetch s.artist ar where s.title like %:keyword% or s.lyrics like %:keyword% or " +
            "s.songWriter like %:keyword% or s.lyricist like %:keyword% or s.genre  like %:keyword% or " +
            "al.title like %:keyword% or al.distributor like %:keyword% or al.agency like %:keyword% or al.intro like %:keyword% or al.category  like %:keyword% or " +
            "ar.name like %:keyword% or  ar.artistType like %:keyword%")
    Optional<List<Song>> findByKeyword(@Param("keyword") String keyword);

    // 자동 검색
    @Query("select s,ar,al from Song s join fetch s.album al join fetch s.artist ar where s.title like %:keyword% or al.title like %:keyword%  or ar.name like %:keyword%")
    Optional<List<Song>> findByAutoComplete(@Param("keyword") String keyword);

    // 관리자 곡 목록보기
    @Query("SELECT new org.example.projectspringfalling.admin.AdminResponse$SongListDTO(s.id, a.id,a.albumImg, s.title, s.artist, a.title) FROM Song s, Album a WHERE s.album.id = a.id")
    List<AdminResponse.SongListDTO> findSongList();

    // 관리자 곡 상세보기
    @Query("SELECT new org.example.projectspringfalling.admin.AdminResponse$SongDetailDTO(s.id, a.albumImg, s.title, s.artist, a.title, s.songWriter,s.lyricist,s.lyrics) FROM Song s, Album a WHERE s.id = :songId AND s.album.id = a.id")
    AdminResponse.SongDetailDTO findOneSongById(Integer songId);

    // 관리자 앨범 상세보기의 장르들
    @Query("SELECT DISTINCT s.genre FROM Song s, Album a WHERE s.album.id = a.id AND a.id = :albumId")
    List<String> findAlbumGenre(Integer albumId);
}
