package org.example.projectspringfalling.admin;

import lombok.RequiredArgsConstructor;
import org.example.projectspringfalling._core.errors.exception.Exception404;
import org.example.projectspringfalling.album.AlbumRepository;
import org.example.projectspringfalling.artist.ArtistRepository;
import org.example.projectspringfalling.report.ReportRepository;
import org.example.projectspringfalling.song.SongRepository;
import org.example.projectspringfalling.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.projectspringfalling._core.utils.ArrayUtil.removeDuplicates;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final ReportRepository reportRepository;

    // 관리자 로그인
    public Admin login(AdminRequest.LoginDTO reqDTO) {
        Admin sessionAdmin = adminRepository.findByEmailAndPassword(reqDTO.getEmail(), reqDTO.getPassword())
                .orElseThrow(() -> new Exception404("아이디 또는 비밀번호가 틀렸습니다."));
        return sessionAdmin;
    }

    // 아티스트 상세보기
    public AdminResponse.ArtistDetailDTO getArtist(Integer artistId) {
        AdminResponse.ArtistDetailDTO artistDetailDTO = artistRepository.findOneArtist(artistId);
        artistDetailDTO.setArtistGenre(removeDuplicates(artistRepository.findArtistGenres(artistId)));
        artistDetailDTO.setArtistAlbums(artistRepository.findAllArtistAlbum(artistId));

        return artistDetailDTO;
    }

    // 아티스트 목록보기
    public List<AdminResponse.ArtistListDTO> getArtistList() {
        return artistRepository.findAllArtist();
    }

    // 앨범 상세보기
    public AdminResponse.AlbumDetailDTO getAlbum(Integer albumId) {
        AdminResponse.AlbumDetailDTO albumDetailDTO = albumRepository.findOneAlbum(albumId);

        albumDetailDTO.setGenres(removeDuplicates(songRepository.findAlbumGenre(albumId)));

        return albumDetailDTO;
    }

    // 앨범 목록보기
    public List<AdminResponse.AlbumListDTO> getAlbumList() {
        return albumRepository.findAlbumList();
    }

    // 곡 상세보기
    public AdminResponse.SongDetailDTO getSong(Integer songId) {
        return songRepository.findOneSongById(songId);
    }

    // 곡 리스트
    public List<AdminResponse.SongListDTO> getSongList() {
        return songRepository.findSongList();
    }

    // 유저 상세보기
    public AdminResponse.UserDetailDTO getUser(int userId) {
        return userRepository.findOneUserById(userId);
    }

    // 유저 목록보기
    public AdminResponse.UserListDTO getUserList() {
        return new AdminResponse.UserListDTO(userRepository.findUserList());
    }

    // 신고 목록보기
    public List<AdminResponse.ReportedList> getReportList() {
        return reportRepository.findAllReportedList();
    }

    // 신고 상세보기
    public AdminResponse.ReportedDetailDTO getReportById(Integer reportId) {
        return reportRepository.findOneReport(reportId);
    }
}
