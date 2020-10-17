package de.google.translator;

import java.sql.ResultSet;
import java.util.List;

import com.cybozu.labs.langdetect.DetectorFactory;

import de.google.db.DBConnection;
import de.google.translator.nlp.tokenizer.StandfordNaturalLanguageProcessor;

public class NLPTest
{

	private static final GoogleTranslator googleTranslator = new GoogleTranslatorImpl();
	private static final StandfordNaturalLanguageProcessor nlpProcessor = new StandfordNaturalLanguageProcessor();

	public static void main( String[] args )
	{

		try
		{

			String workingDir = System.getProperty( "user.dir" );
			DetectorFactory.loadProfile( workingDir + "/profiles/" );

			ResultSet rs = DBConnection.getDbCon().query(
					"SELECT eng_title,eng_review FROM rusl.hotel_reviews WHERE src_language='en' GROUP BY eng_title" );
			while ( rs.next() )
			{
				String title = rs.getString( "eng_title" );
				String review_original = rs.getString( "eng_review" );
				StringBuilder review = new StringBuilder();
				List<String> singleSentences = nlpProcessor.determineSentences( review_original );
				for ( String sentence : singleSentences )
				{

					review.append( sentence + " " );

				}

				System.out.println( title );
				System.out.println( "+++" );
				System.out.println( review_original );
				System.out.println( "+++" );
				System.out.println( review );
				System.out.println( "****************************************************\n" );

				DBConnection.getDbCon().nlpTestUpdate( title, review.toString() );

			}

			System.out.println( "*********** Done **********" );
		}
		catch ( Exception e )
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
