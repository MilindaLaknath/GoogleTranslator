package de.google.translator;

import java.sql.ResultSet;
import java.util.List;

import de.google.db.DBConnection;
import de.google.translator.nlp.tokenizer.StandfordNaturalLanguageProcessor;

public class SentenceBreaker
{

	private static final GoogleTranslator googleTranslator = new GoogleTranslatorImpl();
	private static final StandfordNaturalLanguageProcessor nlpProcessor = new StandfordNaturalLanguageProcessor();

	public static void main( String[] args )
	{

		try
		{

			ResultSet rs = DBConnection.getDbCon()
					.query( " SELECT reviewsId,review_eng FROM hotel_review.translated " );

			while ( rs.next() )
			{

				int reviewId = rs.getInt( "reviewsId" );
				String reviewEng = rs.getString( "review_eng" );

				List<String> singleSentences = nlpProcessor.determineSentences( reviewEng );
				for ( String sentence : singleSentences )
				{
					DBConnection.getDbCon().saveSentence( reviewId, sentence );
					System.out.println( sentence + "\n" );
				}
				System.out.println( "one review done\n" );
			}

			System.out.println( "*********** all Done **********" );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

	}

}
