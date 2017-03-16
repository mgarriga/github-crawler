# github-crawler

Crawler for GitHub projects. Initial version.

The crawler fetches projects by keyword (query) and language (e.g., java/javascript) by using the GitHub Java Api.
Then Downloads a .zip file for each project found. Notice that the GitHub Api limits the results to 100 per page, 1000 in total (10 pages)
