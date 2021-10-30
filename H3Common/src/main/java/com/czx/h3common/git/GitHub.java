package com.czx.h3common.git;

import com.czx.h3common.git.dto.*;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface GitHub {
    @RequestLine("GET /repos/{owner}/{repo}")
    @Headers("Accept: application/vnd.github.v3+json")
    RepoEntityDto getRepo(@Param("owner") String owner, @Param("repo") String repo);

    @RequestLine("GET /user/repos")
    @Headers("Accept: application/vnd.github.v3+json")
    List<RepoEntityDto> getAuthUserRepos(@QueryMap RepoQueryDto req);

    @RequestLine("GET /users/{username}/repos")
    @Headers("Accept: application/vnd.github.v3+json")
    List<RepoEntityDto> listUserRepos(@Param("username") String owner, RepoQueryDto req);

    @RequestLine("POST /user/repos")
    @Headers("Accept: application/vnd.github.v3+json")
    Map<String, Object> createRepo(RepoDto repoDto);

    @RequestLine("DELETE /repos/{owner}/{repo}")
    @Headers("Accept: application/vnd.github.v3+json")
    Map<String, Object> deleteRepo(@Param("owner") String owner, @Param("repo") String repo);

    @RequestLine("GET /rate_limit")
    @Headers("Accept: application/vnd.github.v3+json")
    Map<String, Object> rateLimit();

    @RequestLine("PUT /repos/{owner}/{repo}/contents/{path}")
    @Headers("Accept: application/vnd.github.v3+json")
    FileMetaDto putFile(@Param("owner") String owner,
                               @Param("repo") String repo,
                               @Param("path") String path,
                               FileDto obj);

    @RequestLine("DELETE /repos/{owner}/{repo}/contents/{path}")
    @Headers("Accept: application/vnd.github.v3+json")
    Map<String,Object> deleteFile(@Param("owner") String owner,
                                  @Param("repo") String repo,
                                  @Param("path") String path,
                                  FileDto obj);


    @RequestLine("POST /repos/{owner}/{repo}/git/trees")
    @Headers("Accept: application/vnd.github.v3+json")
    TreeDto createTree(@Param("owner") String owner,
                       @Param("repo") String repo,
                       CreateTreeDto tree);

    @RequestLine("GET /repos/{owner}/{repo}/git/trees/{tree_sha}")
    @Headers("Accept: application/vnd.github.v3+json")
    TreeDto getGitTree(@Param("owner") String owner,
                       @Param("repo") String repo,
                       @Param("tree_sha") String sha,
                       @QueryMap Map<String,Object> query);


    @RequestLine("POST /repos/{owner}/{repo}/git/blobs")
    @Headers("Accept: application/vnd.github.v3+json")
    BlobDto createBlob(@Param("owner") String owner,
                       @Param("repo") String repo,
                       BlobDto blob);

    @RequestLine("GET /repos/{owner}/{repo}/git/blobs/{file_sha}")
    @Headers("Accept: application/vnd.github.v3+json")
    BlobDto getBlob(@Param("owner") String owner,
                    @Param("repo") String repo,
                    @Param("file_sha") String sha);

    @RequestLine("GET /repos/{owner}/{repo}/git/ref/{ref}")
    @Headers("Accept: application/vnd.github.v3+json")
    RefDto getRef(@Param("owner") String owner,
                  @Param("repo") String repo,
                  @Param("ref") String ref);

    @RequestLine("POST /repos/{owner}/{repo}/git/refs/{ref}")
    @Headers("Accept: application/vnd.github.v3+json")
    RefDto updateRef(@Param("owner") String owner,
                     @Param("repo") String repo,
                     @Param("ref") String ref,
                     Map<String, Object> content);


    @RequestLine("GET /repos/{owner}/{repo}/git/commits/{commit_sha}")
    @Headers("Accept: application/vnd.github.v3+json")
    CommitDto getCommit(@Param("owner") String owner,
                        @Param("repo") String repo,
                        @Param("commit_sha") String commit_sha);


    @RequestLine("POST /repos/{owner}/{repo}/git/commits")
    @Headers("Accept: application/vnd.github.v3+json")
    CommitDto createCommit(@Param("owner") String owner,
                           @Param("repo") String repo,
                           Map<String, Object> content);

}
