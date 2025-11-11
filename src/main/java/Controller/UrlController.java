package Controller;

import Dto.UrlDto;
import Entities.Url;
import Repositories.IUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
public class UrlController {

    @Autowired
    private IUrlRepository urlRepository;

    @PostMapping
    public ResponseEntity<Url> store(@RequestBody UrlDto urlDto){
        Url url = new Url();
        url.setUrl(urlDto.getUrl());
        url.setShortCode(urlDto.getShortUrl());
        return ResponseEntity.ok(urlRepository.save(url));
    }

//    @RequestMapping("/redirect/{shordUrl}")
//    public void redirect(@RequestParam String shordUrl){
//
//    }
}
