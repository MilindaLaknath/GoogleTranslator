package de.google.translator;

import java.sql.ResultSet;
import java.util.List;
import java.util.Locale;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;

import de.google.db.DBConnection;
import de.google.translator.nlp.tokenizer.StandfordNaturalLanguageProcessor;

public class TranslatorMain
{

	private static final GoogleTranslator googleTranslator = new GoogleTranslatorImpl();
	private static final StandfordNaturalLanguageProcessor nlpProcessor = new StandfordNaturalLanguageProcessor();

	public static void main( String[] args )
	{

		try
		{

			String workingDir = System.getProperty( "user.dir" );
			DetectorFactory.loadProfile( workingDir + "/profiles/" );

			// ResultSet rs = DBConnection.getDbCon()
			// .query( " SELECT * FROM rusl.hotel_reviews WHERE idnew_table < 50 GROUP BY title" );

			ResultSet rs = DBConnection.getDbCon()
					.query( " SELECT * FROM hotel_review.reviews GROUP BY review,hotel_name ORDER BY hotel_name " );

			int i = 1;
			while ( rs.next() )
			{
				Detector detect = DetectorFactory.create();

				String title = rs.getString( "title" );
				String review_original = rs.getString( "review" );
				String hotel = rs.getString( "hotel_name" );
				String title_eng = "";
				String language = "en";
				if ( !review_original.equals( "" ) )
				{
					detect.append( review_original );
					language = detect.detect();
				}
				StringBuilder review = new StringBuilder();

				List<String> singleSentences = nlpProcessor.determineSentences( review_original );
				for ( String sentence : singleSentences )
				{
					if ( !language.equals( "en" ) )
					{
						title_eng = googleTranslator.translate( Locale.ENGLISH, title ).get();
						review.append( googleTranslator.translate( Locale.ENGLISH, sentence ).get() + " " );
					}
					else
					{
						title_eng = title;
						review.append( sentence + " " );
					}
				}

				System.out.println( ( i++ ) + language );
				System.out.println( title );
				System.out.println( title_eng );
				System.out.println( "" );
				System.out.println( review_original );
				System.out.println( "" );
				System.out.println( singleSentences );
				System.out.println( "" );
				System.out.println( review );
				System.out.println( "****************************************************\n" );

				DBConnection.getDbCon().update( title, title_eng, review.toString(), language, hotel );

			}

			System.out.println( "*********** Done **********" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

	}

}
