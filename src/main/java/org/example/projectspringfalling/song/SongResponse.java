package org.example.projectspringfalling.song;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectspringfalling.album.Album;
import org.example.projectspringfalling.artist.Artist;
import org.example.projectspringfalling.playlist.Playlist;

import java.sql.Timestamp;
import java.util.List;

public class SongResponse {

    // 메인페이지
    @NoArgsConstructor
    @Data
    public static class MainDTO {
        private List<LatestAlbumDTO> latestAlbumList; // 최신 앨범
        private List<AdminPlaylistDTO> newPlaylist; // 관리자 플레이리스트
        private List<AdminPlaylistDTO> adminPlaylist; // 관리자 플레이리스트

        public MainDTO(List<LatestAlbumDTO> latestAlbumList, List<AdminPlaylistDTO> newPlaylist, List<AdminPlaylistDTO> adminPlaylist) {
            this.latestAlbumList = latestAlbumList;
            this.newPlaylist = newPlaylist;
            this.adminPlaylist = adminPlaylist;
        }

        // 최신 앨범
        @NoArgsConstructor
        @Data
        public static class LatestAlbumDTO {
            private Integer albumId; // 앨범 pk
            private String albumImage; // 앨범 이미지
            private String albumTitle; // 앨범 이름
            private Integer artistId; // 아티스트 pk
            private String artistName; // 아티스트 이름

            public LatestAlbumDTO(Album album, Artist artist) {
                this.albumId = album.getId();
                this.albumImage = album.getAlbumImg();
                this.albumTitle = album.getTitle();
                this.artistId = artist.getId();
                this.artistName = artist.getName();
            }
        }

        // 관리자의 최신 플레이리스트
        // 관리자 플레이리스트
        @NoArgsConstructor
        @Data
        public static class AdminPlaylistDTO {
            private Integer playlistId; // 플레이리스트 pk
            private String playlistImg; // 플레이리스트 이미지
            private String playlistTitle; // 플레이리스트 이름

            public AdminPlaylistDTO(Playlist playlist, String playlistImg) {
                this.playlistId = playlist.getId();
                this.playlistImg = playlistImg;
                this.playlistTitle = playlist.getName();
            }
        }
    }

    // 노래 상세보기 DTO
    @NoArgsConstructor
    @Data
    public static class DetailDTO {
        private Integer songId; // 노래 pk
        private String title; // 노래 제목
        private Integer albumId; // 앨범 pk
        private String albumImage; // 앨범 커버 이미지
        private String artist; // 가수 이름
        private String albumTitle; // 앨범 제목
        private String songWriter; // 작곡가
        private String lyricist; // 작사가
        private String lyrics; // 가사
        private String musicFile; // 음악 파일


        public DetailDTO(Song song, Album album) {
            this.songId = song.getId();
            this.title = song.getTitle();
            this.albumId = album.getId();
            this.albumImage = album.getAlbumImg();
            this.artist = song.getArtist().getName();
            this.albumTitle = album.getTitle();
            this.songWriter = song.getSongWriter();
            this.lyricist = song.getLyricist();
            this.lyrics = song.getLyrics();
        }
    }

    // 앨범 수록곡 리스트 DTO
    @NoArgsConstructor
    @Data
    public static class AlbumListDTO {
        private Integer songId; // 노래 pk
        private Integer index; // 리스트 index 번호
        private String title; // 노래 제목
        private String musicVideo; // 뮤직비디오
        private Boolean isTitle; // 타이틀 여부
        private String musicFile; // 음악 파일
        private Integer artistId; // 가수
        private String artistName; // 가수 이름

        public AlbumListDTO(Song song, Integer index) {
            this.songId = song.getId();
            this.index = index;
            this.title = song.getTitle();
            this.musicVideo = song.getMusicVideo();
            this.isTitle = song.getIsTitle();
            this.musicFile = song.getMusicFile();
            this.artistId = new ArtistDTO(song.getArtist()).artistId;
            this.artistName = new ArtistDTO(song.getArtist()).artistName;
        }

        public static class ArtistDTO {
            private Integer artistId; // 가수 pk
            private String artistName; // 가수 이름

            public ArtistDTO(Artist artist) {
                this.artistId = artist.getId();
                this.artistName = artist.getName();
            }
        }
    }

    @Data
    public static class SearchDTO {

        private String keyword;
        private ArtistDTO artistDTO;
        private List<SongDTO> songs;

        public SearchDTO(List<Song> songList, String keyword) {
            this.keyword = keyword;
            if (!songList.isEmpty()) {
                this.artistDTO = new ArtistDTO(songList.get(songList.size() - 1));
            } else {
                this.artistDTO = null; // 또는 적절한 기본값 설정
            }
            this.songs = songList.stream().map(song -> new SongDTO(song)).toList();
        }

        @Data
        public class ArtistDTO {
            private Integer songId;
            private Integer artistId;
            private Integer albumId;
            private String albumImg;
            private String artistName;
            private String albumTitle;
            private String songTitle;

            public ArtistDTO(Song song) {
                this.songId = song.getId();
                this.artistId = song.getArtist().getId();
                this.albumId = song.getAlbum().getId();
                this.albumImg = song.getAlbum().getAlbumImg();
                this.artistName = song.getArtist().getName();
                this.albumTitle = song.getAlbum().getTitle();
                this.songTitle = song.getTitle();
            }
        }

        @Data
        public class SongDTO {
            private Integer songId;
            private Integer artistId;
            private Integer albumId;
            private String songTitle;
            private String albumTitle;
            private String artistName;
            private String albumImg;
            private String artistImg;
            private String lyrics;
            private String genre;
            private String category;
            private String artistType;
            private String nationality;
            private Timestamp albumCreatedAt;

            public SongDTO(Song song) {
                this.songId = song.getId();
                this.artistId = song.getArtist().getId();
                this.albumId = song.getAlbum().getId();
                this.songTitle = song.getTitle();
                this.albumTitle = song.getAlbum().getTitle();
                this.artistName = song.getArtist().getName();
                this.albumImg = song.getAlbum().getAlbumImg();
                this.artistImg = song.getArtist().getArtistImg();
                this.lyrics = song.getLyrics();
                this.genre = song.getGenre();
                this.nationality = song.getAlbum().getNationality();
                this.artistType = song.getArtist().getArtistType();
                this.category = song.getAlbum().getCategory();
                this.albumCreatedAt = song.getAlbum().getCreatedAt();
            }
        }
    }
}
