package com.mes.AliasFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;

public class AliasFinder {

	public static ArrayList<String> authorListFilter = new ArrayList();
	public static ArrayList<String> Alias = new ArrayList();

	private static void removeRepeatedAuthors(ArrayList<String> authorListLines) {

		authorListFilter.add(authorListLines.get(0));
		for (int i = 1; i < authorListLines.size(); i++) {

			Boolean flag = Boolean.TRUE;
			String s = authorListLines.get(i);

			for (int j = 0; j < authorListFilter.size(); j++) {
				//System.out.println(j);
				if (s.equalsIgnoreCase(authorListFilter.get(j)))
					flag = Boolean.FALSE;
			}
			if (flag)
				authorListFilter.add(s);

		}
	}

	private static void heuristicEquals() {

		Boolean flagAliasName = Boolean.FALSE;
		Boolean flagAliasEmail = Boolean.FALSE;
		ArrayList<Integer> toRemove = new ArrayList();

		for (int i = 0; i < authorListFilter.size(); i++) {

			String author = authorListFilter.get(i);
			authorListFilter.remove(i);

			String authorName = author.substring(0, author.indexOf(';'));
			String authorEmail = author.substring(author.indexOf(';'), author.length());

			String result = new String();
			result = "Author; " + author;

			for (int j = 0; j < authorListFilter.size(); j++) {
				String alias = authorListFilter.get(j);
				String aliasName = alias.substring(0, alias.indexOf(';'));
				String aliasEmail = alias.substring(alias.indexOf(';'), alias.length());

				// Same Name?
				if (authorName.equalsIgnoreCase(aliasName)) {
					flagAliasName = Boolean.TRUE;
				}

				// Same e-mail?
				if (authorEmail.equalsIgnoreCase(aliasEmail)) {
					flagAliasEmail = Boolean.TRUE;
				}

				// HeuristicEquals
				if (flagAliasName || flagAliasEmail) {
					result = result + " Alias;" + alias;
					toRemove.add(j);
				}

				flagAliasName = Boolean.FALSE;
				flagAliasEmail = Boolean.FALSE;

			}
			// Updating the list
			for (Integer k : toRemove) {
				authorListFilter.remove(k);
			}
			//System.out.println(result);
			Alias.add(result);
		}

	}

	public static void main(String[] args) throws IOException {

		ArrayList<Author> authorList = new ArrayList<Author>();
		File file = new File("authorList.txt");
		FileOutputStream fop = new FileOutputStream(file);

		// Basic authentication
		GitHubClient client = new GitHubClient();
		client.setCredentials(args[0], args[1]);

		//Download Commits
		final int size = 25;
		final RepositoryId repo = new RepositoryId("github", args[2]);
		final CommitService service = new CommitService(client);
		int pages = 1;

		for (Collection<RepositoryCommit> commits : service.pageCommits(repo, size)) {
			System.out.println("Commit Page " + pages++);

			for (RepositoryCommit commit : commits) {

				String author = commit.getCommit().getAuthor().getName();
				String email = commit.getCommit().getAuthor().getEmail();

				String info = author + ";" + email + "; \n";
				System.out.println(info);

				Author authorAux = new Author(author, email);
				authorList.add(authorAux);

				try {
					if (!file.exists()) {
						file.createNewFile();
					}

					byte[] contentInBytes = info.getBytes();
					fop.write(contentInBytes);
					fop.flush();

				} catch (Exception e) {
					System.out.println(e.toString());
				}
			}
		}
		fop.close();

		// Reading AuthorList
		ArrayList<String> authorListLines = new ArrayList();

		BufferedReader br = new BufferedReader(new FileReader("authorList.txt"));
		String line = br.readLine();

		while (line != null) {
			authorListLines.add(line);
			line = br.readLine();
		}

		//Filtering
		removeRepeatedAuthors(authorListLines);
		heuristicEquals();

		File file2 = new File("AliasList.txt");
		FileOutputStream fop2 = new FileOutputStream(file2);
		
		for(String s : Alias){
			try {
				if (!file2.exists()) 
					file2.createNewFile();
				
				s=s+"\n";

				byte[] contentInBytes = s.getBytes();
				fop2.write(contentInBytes);
				fop2.flush();

			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		fop2.close();
	}
}
