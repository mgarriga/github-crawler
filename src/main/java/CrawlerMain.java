/**
 * Created by tincho on 15/03/17.
 */

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CrawlerMain {
    public static void main (String[] args){
        //Basic authentication if needed
        GitHubClient client = new GitHubClient();
        client.setCredentials("user", "password");

        // add client as a parameter if authentication is needed
        RepositoryService service = new RepositoryService();

        try {

            int n= 0;

            for (int i = 1; i<11; i++){

                //bring 100 repositories -- here the query keyword and language can be changed. E.g. "JavaScript"
                List<SearchRepository> repos = service.searchRepositories("microservices","Java",i);

                ExecutorService executorService = Executors.newCachedThreadPool();

                for (SearchRepository repo : repos){
                    n++;
                    // ATTENTION: this path should exist and should be changed according to the language configured above
                    File file = new File("./Java/"+repo.getName()+"-"+n+".zip");

                    //Download 100 repositories in parallel
                    executorService.submit(()->{

                        System.out.println("Downloading... " + repo.getName() + " -- " + repo.getUrl().toString());
                        try {
                            //Get the url of the .zip file with the latest version of the master branch for the current repo
                            URL url = new URL(repo.getUrl()+"/archive/master.zip");
                            FileUtils.copyURLToFile(url,file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("Finished! " + file.toString());
                    });
                }
                try {
                    System.out.println("attempt to shutdown executor");
                    executorService.shutdown();
                    //Download Timeout every 100 services -- default 6 minutes (thus processing a maximum of 1000 will demand one hour).
                    // With TO=6, approx. 95% of the downloads finished successfully
                    executorService.awaitTermination(6, TimeUnit.MINUTES);
                }
                catch (InterruptedException e) {
                    System.err.println("tasks interrupted");
                }
                finally {
                    if (!executorService.isTerminated()) {
                        System.err.println("cancel non-finished tasks");
                    }
                    executorService.shutdownNow();
                    System.out.println("shutdown finished");
                }
                System.out.println();
            }
            System.out.println("N° of repos: " + n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
