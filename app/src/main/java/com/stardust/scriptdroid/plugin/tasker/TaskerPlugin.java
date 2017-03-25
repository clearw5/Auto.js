//package com.yourcompany.yourcondition;
//package com.yourcompany.yoursetting;
package com.stardust.scriptdroid.plugin.tasker;

// Constants and functions for Tasker *extensions* to the plugin protocol
// See Also: http://tasker.dinglisch.net/plugins.html

// Release Notes

// v1.1 20140202
// added function variableNameValid()
// fixed some javadoc entries (thanks to David Stone)

// v1.2 20140211
// added ACTION_EDIT_EVENT

// v1.3 20140227
// added REQUESTED_TIMEOUT_MS_NONE, REQUESTED_TIMEOUT_MS_MAX and REQUESTED_TIMEOUT_MS_NEVER
// requestTimeoutMS(): added range check

// v1.4 20140516
// support for data pass through in REQUEST_QUERY intent
// some javadoc entries fixed (thanks again David :-))

// v1.5 20141120
// added RESULT_CODE_FAILED_PLUGIN_FIRST
// added Setting.VARNAME_ERROR_MESSAGE

// v1.6 20150213
// added Setting.getHintTimeoutMS()
// added Host.addHintTimeoutMS()

// v1.7 20160619
// null check for getCallingActivity() in hostSupportsOnFireVariableReplacement( Activity editActivity )

// v1.8 20161002
// added hostSupportsKeyEncoding(), setKeyEncoding() and Host.getKeysWithEncoding()

import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
 
public class TaskerPlugin {

	private final static String 	TAG = "TaskerPlugin"; 

	private final static String 	BASE_KEY = "net.dinglisch.android.tasker";
	
	private final static String 	EXTRAS_PREFIX = BASE_KEY + ".extras.";

	private final static int		FIRST_ON_FIRE_VARIABLES_TASKER_VERSION = 80;
	
	public final static String		VARIABLE_PREFIX = "%"; 
	
	// when generating non-repeating integers, look this far back for repeats
	// see getPositiveNonRepeatingRandomInteger()
	private final static int		RANDOM_HISTORY_SIZE = 100;
	
	/**
     * 	Action that the EditActivity for an event plugin should be launched by
     */
	public final static String 		ACTION_EDIT_EVENT = BASE_KEY + ".ACTION_EDIT_EVENT";
	
	private final static String		VARIABLE_NAME_START_EXPRESSION =  "[\\w&&[^_]]";
	private final static String		VARIABLE_NAME_MID_EXPRESSION =  "[\\w0-9]+";
	private final static String		VARIABLE_NAME_END_EXPRESSION =  "[\\w0-9&&[^_]]";

	public final static String		VARIABLE_NAME_MAIN_PART_MATCH_EXPRESSION =
			VARIABLE_NAME_START_EXPRESSION + VARIABLE_NAME_MID_EXPRESSION + VARIABLE_NAME_END_EXPRESSION
	;

	public final static String		VARIABLE_NAME_MATCH_EXPRESSION =
			VARIABLE_PREFIX + "+" +
			VARIABLE_NAME_MAIN_PART_MATCH_EXPRESSION
	;

	private static Pattern			VARIABLE_NAME_MATCH_PATTERN = null; 
	
	/**
	 *	@see #addVariableBundle(Bundle, Bundle)  
	 *	@see Host#getVariablesBundle(Bundle)
	 */
	private final static String		EXTRA_VARIABLES_BUNDLE = EXTRAS_PREFIX + "VARIABLES";

	/**
     * 	Host capabilities, passed to plugin with edit intents 
     */
	private final static String		EXTRA_HOST_CAPABILITIES = EXTRAS_PREFIX + "HOST_CAPABILITIES";

	/**
     *  @see Setting#hostSupportsVariableReturn(Bundle)
     */
	public final static int			EXTRA_HOST_CAPABILITY_SETTING_RETURN_VARIABLES = 2;
	
	/**
     *	@see Condition#hostSupportsVariableReturn(Bundle)
     */
	public final static int			EXTRA_HOST_CAPABILITY_CONDITION_RETURN_VARIABLES = 4;

	/**
     * 	@see Setting#hostSupportsOnFireVariableReplacement(Bundle)
     */
	public final static int			EXTRA_HOST_CAPABILITY_SETTING_FIRE_VARIABLE_REPLACEMENT = 8;

	/**
     * @see Setting#hostSupportsVariableReturn(Bundle)
     */
	private final static int		EXTRA_HOST_CAPABILITY_RELEVANT_VARIABLES = 16;

	public final static int			EXTRA_HOST_CAPABILITY_SETTING_SYNCHRONOUS_EXECUTION = 32;
	
	public final static int			EXTRA_HOST_CAPABILITY_REQUEST_QUERY_DATA_PASS_THROUGH = 64;
	
	public final static int			EXTRA_HOST_CAPABILITY_ENCODING_JSON = 128;
	
	public final static int			EXTRA_HOST_CAPABILITY_ALL = 
			EXTRA_HOST_CAPABILITY_SETTING_RETURN_VARIABLES | 
			EXTRA_HOST_CAPABILITY_CONDITION_RETURN_VARIABLES |
			EXTRA_HOST_CAPABILITY_SETTING_FIRE_VARIABLE_REPLACEMENT |
			EXTRA_HOST_CAPABILITY_RELEVANT_VARIABLES|
			EXTRA_HOST_CAPABILITY_SETTING_SYNCHRONOUS_EXECUTION |
			EXTRA_HOST_CAPABILITY_REQUEST_QUERY_DATA_PASS_THROUGH |
			EXTRA_HOST_CAPABILITY_ENCODING_JSON
	;

	/**
	 * Possible encodings of text in bundle values
	 * 
	 * @see #setKeyEncoding(Bundle,String[],Encoding)
	 */
	public enum Encoding { JSON };

	private final static String		BUNDLE_KEY_ENCODING_JSON_KEYS = BASE_KEY + ".JSON_ENCODED_KEYS";

	public static boolean hostSupportsKeyEncoding( Bundle extrasFromHost, Encoding encoding ) {
		switch ( encoding ) {
		case JSON:
			return hostSupports( extrasFromHost, EXTRA_HOST_CAPABILITY_ENCODING_JSON );
		default:
			return false;
		}
	}
	
	/**
     * 	
     *  Miscellaneous operational hints going one way or the other
     *  @see Setting#hostSupportsVariableReturn(Bundle)
     */
	
	private final static String		EXTRA_HINTS_BUNDLE = EXTRAS_PREFIX + "HINTS";

	private final static String		BUNDLE_KEY_HINT_PREFIX = ".hints.";
	
	private final static String		BUNDLE_KEY_HINT_TIMEOUT_MS = BUNDLE_KEY_HINT_PREFIX + "TIMEOUT";
	
	/**
	 * 
     *	@see #hostSupportsRelevantVariables(Bundle)
     *  @see #addRelevantVariableList(Intent, String[])
     *  @see #getRelevantVariableList(Bundle)
     */
	private final static String	BUNDLE_KEY_RELEVANT_VARIABLES = BASE_KEY + ".RELEVANT_VARIABLES";

	public static boolean hostSupportsRelevantVariables( Bundle extrasFromHost ) {
		return hostSupports( extrasFromHost,  EXTRA_HOST_CAPABILITY_RELEVANT_VARIABLES );
	}
	
	/**
 	* Specifies to host which variables might be used by the plugin.
 	* 
 	* Used in EditActivity, before setResult().
 	*
 	* @param  intentToHost the intent being returned to the host
 	* @param  variableNames array of relevant variable names
 	*/
	public static void addRelevantVariableList( Intent intentToHost, String [] variableNames ) {
		intentToHost.putExtra( BUNDLE_KEY_RELEVANT_VARIABLES, variableNames );
	}

	/**
 	* Validate a variable name.
 	* 
 	* The basic requirement for variables from a plugin is that they must be all lower-case.
 	* 
 	* @param  varName name to check
 	*/
    public static boolean variableNameValid( String varName ) {

    	boolean validFlag = false;
    	
    	if ( varName == null )
    		Log.d( TAG, "variableNameValid: null name" );
    	else {
    		if ( VARIABLE_NAME_MATCH_PATTERN == null )
    			VARIABLE_NAME_MATCH_PATTERN = Pattern.compile( VARIABLE_NAME_MATCH_EXPRESSION, 0 );
    	
    		if ( VARIABLE_NAME_MATCH_PATTERN.matcher( varName ).matches() ) {

    			if ( variableNameIsLocal( varName ) )
    				validFlag = true;
    			else
    				Log.d( TAG, "variableNameValid: name not local: " + varName );
    		}
    		else
    			Log.d( TAG, "variableNameValid: invalid name: " + varName );
    	}
    	
    	return validFlag;
    }

	/**
	 * Allows the plugin/host to indicate to each other a set of variables which they are referencing.
	 * The host may use this to e.g. show a variable selection list in it's UI.
	 * The host should use this if it previously indicated to the plugin that it supports relevant vars
	 *
	 * @param  fromHostIntentExtras usually from getIntent().getExtras() 
	 * @return variableNames an array of relevant variable names
	*/
	public static String [] getRelevantVariableList( Bundle fromHostIntentExtras ) {

		String [] relevantVars = (String []) getBundleValueSafe( fromHostIntentExtras, BUNDLE_KEY_RELEVANT_VARIABLES, String [].class, "getRelevantVariableList" );
		
		if ( relevantVars == null )
			relevantVars = new String [0];
		
		return relevantVars;
	}

	/**
	 * Used by: plugin QueryReceiver, FireReceiver
	 *
	 * Add a bundle of variable name/value pairs.
	 * 
	 * Names must be valid Tasker local variable names.
	 * Values must be String, String [] or ArrayList<String>
	 * Null values cause deletion of possible already-existing variables. 
	 *
	 * @param resultExtras the result extras from the receiver onReceive (from a call to getResultExtras())
	 * @param variables the variables to send
	 * @see Setting#hostSupportsVariableReturn(Bundle)
	 * @see #variableNameValid(String)
	*/
	public static void addVariableBundle( Bundle resultExtras, Bundle variables ) {
		resultExtras.putBundle( EXTRA_VARIABLES_BUNDLE, variables );
	}
	
	/**
	 * Used by: plugin EditActivity
	 *
	 * Specify the encoding for a set of bundle keys.
	 * 
	 * This is completely optional and currently only necessary if using Setting#setVariableReplaceKeys
	 * where the corresponding values of some of the keys specified are JSON encoded.
	 * 
	 * @param  resultBundleToHost the bundle being returned to the host
	 * @param keys the keys being returned to the host which are encoded in some way
	 * @param encoding the encoding of the values corresponding to the specified keys
	 * @see #setVariableReplaceKeys(Bundle,String[])
	 * @see #hostSupportsKeyEncoding(Bundle, Encoding)
	*/
	public static void setKeyEncoding( Bundle resultBundleToHost, String [] keys, Encoding encoding ) {
		if ( Encoding.JSON.equals( encoding ) )
			addStringArrayToBundleAsString( 
					keys, resultBundleToHost, BUNDLE_KEY_ENCODING_JSON_KEYS, "setValueEncoding"
			);
		else
			Log.e( TAG, "unknown encoding: " + encoding );
	}

	// ----------------------------- SETTING PLUGIN ONLY --------------------------------- //

	public static class Setting {

		/**
	     * 	Variable name into which a description of any error that occurred can be placed
	     *  for the user to process.
	     *   
	     *  Should *only* be set when the BroadcastReceiver result code indicates a failure.
		 *
	     *  Note that the user needs to have configured the task to continue after failure of the plugin
	     *  action otherwise they will not be able to make use of the error message.
	     *  
	     *  For use with #addRelevantVariableList(Intent, String[]) and #addVariableBundle(Bundle, Bundle)
	     *  
	     */
		public final static String		VARNAME_ERROR_MESSAGE = VARIABLE_PREFIX + "errmsg";

		/**
		 *	@see #setVariableReplaceKeys(Bundle, String[])
	     */
		private final static String		BUNDLE_KEY_VARIABLE_REPLACE_STRINGS = EXTRAS_PREFIX + "VARIABLE_REPLACE_KEYS";

		/**
		 *	@see #requestTimeoutMS(android.content.Intent, int)
	     */
		private final static String 	EXTRA_REQUESTED_TIMEOUT = EXTRAS_PREFIX + "REQUESTED_TIMEOUT";

		/**
		 *	@see #requestTimeoutMS(android.content.Intent, int)
	     */
        
        public final static int 		REQUESTED_TIMEOUT_MS_NONE = 0;

		/**
		 *	@see #requestTimeoutMS(android.content.Intent, int)
	     */
        
        public final static int 		REQUESTED_TIMEOUT_MS_MAX = 3599000;

		/**
		 *	@see #requestTimeoutMS(android.content.Intent, int)
	     */
        
        public final static int 		REQUESTED_TIMEOUT_MS_NEVER = REQUESTED_TIMEOUT_MS_MAX + 1000;

		 /**
         *  @see #signalFinish(Context, Intent, int, Bundle)
         *  @see Host#addCompletionIntent(Intent, Intent)
         */
        private final static String 	EXTRA_PLUGIN_COMPLETION_INTENT = EXTRAS_PREFIX + "COMPLETION_INTENT";

		/**
         *  @see #signalFinish(Context, Intent, int, Bundle)
         *  @see Host#getSettingResultCode(Intent)
         */
        public final static String 		EXTRA_RESULT_CODE = EXTRAS_PREFIX + "RESULT_CODE";

		/**
		*  @see #signalFinish(Context, Intent, int, Bundle)
        *  @see Host#getSettingResultCode(Intent)
        */
        
        public final static int	RESULT_CODE_OK = Activity.RESULT_OK; 
        public final static int	RESULT_CODE_OK_MINOR_FAILURES = Activity.RESULT_FIRST_USER;
        public final static int	RESULT_CODE_FAILED = Activity.RESULT_FIRST_USER + 1;
        public final static int	RESULT_CODE_PENDING = Activity.RESULT_FIRST_USER + 2;
        public final static int	RESULT_CODE_UNKNOWN = Activity.RESULT_FIRST_USER + 3;  
        
        /**
        * If a plugin wants to define it's own error codes, start numbering them here.
        * The code will be placed in an error variable (%err in the case of Tasker) for
        * the user to process after the plugin action.
        */
        
        public final static int	RESULT_CODE_FAILED_PLUGIN_FIRST = Activity.RESULT_FIRST_USER + 9;  
		
        /**
		 * Used by: plugin EditActivity.
		 * 
		 * Indicates to plugin that host will replace variables in specified bundle keys.
		 * 
		 * Replacement takes place every time the setting is fired, before the bundle is
		 * passed to the plugin FireReceiver.
		 *
		 * @param  extrasFromHost intent extras from the intent received by the edit activity
		 * @see #setVariableReplaceKeys(Bundle, String[])
		*/
		public static boolean hostSupportsOnFireVariableReplacement( Bundle extrasFromHost ) {
			return hostSupports( extrasFromHost, EXTRA_HOST_CAPABILITY_SETTING_FIRE_VARIABLE_REPLACEMENT );
		}

		/**
		 * Used by: plugin EditActivity.
		 * 
		 * Description as above.
		 * 
		 * This version also includes backwards compatibility with pre 4.2 Tasker versions.
		 * At some point this function will be deprecated.
		 * 
		 * @param  editActivity the plugin edit activity, needed to test calling Tasker version
		 * @see #setVariableReplaceKeys(Bundle, String[])
		*/

		public static boolean hostSupportsOnFireVariableReplacement( Activity editActivity ) {
			
			boolean supportedFlag = hostSupportsOnFireVariableReplacement( editActivity.getIntent().getExtras() );
			
			if ( ! supportedFlag ) {

				ComponentName callingActivity = editActivity.getCallingActivity();
				
				if ( callingActivity == null )
					Log.w( TAG, "hostSupportsOnFireVariableReplacement: null callingActivity, defaulting to false" );
				else {
					String callerPackage = callingActivity.getPackageName();
				
					// Tasker only supporteed this from 1.0.10
					supportedFlag = 
						( callerPackage.startsWith( BASE_KEY ) ) &&
						( getPackageVersionCode( editActivity.getPackageManager(), callerPackage ) > FIRST_ON_FIRE_VARIABLES_TASKER_VERSION )
					;
				}
			}
			
			return supportedFlag;
		}
		
		public static boolean hostSupportsSynchronousExecution( Bundle extrasFromHost ) {
			return hostSupports( extrasFromHost, EXTRA_HOST_CAPABILITY_SETTING_SYNCHRONOUS_EXECUTION );
		}

		/**
	 	* Request the host to wait the specified number of milliseconds before continuing.
	 	* Note that the host may choose to ignore the request.
	 	* 
	 	* Maximum value is REQUESTED_TIMEOUT_MS_MAX. 
	 	* Also available are REQUESTED_TIMEOUT_MS_NONE (continue immediately without waiting
	 	* for the plugin to finish) and REQUESTED_TIMEOUT_MS_NEVER (wait forever for
	 	* a result).
	 	* 
	 	* Used in EditActivity, before setResult().
	 	*
	 	* @param  intentToHost the intent being returned to the host
	 	* @param  timeoutMS 
	 	*/
		public static void requestTimeoutMS( Intent intentToHost, int timeoutMS ) {
			if ( timeoutMS < 0 )
				Log.w( TAG, "requestTimeoutMS: ignoring negative timeout (" + timeoutMS + ")" );
			else {
				if ( 
						( timeoutMS > REQUESTED_TIMEOUT_MS_MAX ) &&
						( timeoutMS != REQUESTED_TIMEOUT_MS_NEVER )
				) {
					Log.w( TAG, "requestTimeoutMS: requested timeout " + timeoutMS + " exceeds maximum, setting to max (" + REQUESTED_TIMEOUT_MS_MAX + ")" );
					timeoutMS = REQUESTED_TIMEOUT_MS_MAX;
				}
				intentToHost.putExtra( EXTRA_REQUESTED_TIMEOUT, timeoutMS );
			}
		}

		/**
		 * Used by: plugin EditActivity 
		 *
		 * Indicates to host which bundle keys should be replaced.
		 *
		 * @param  resultBundleToHost the bundle being returned to the host
		 * @param  listOfKeyNames which bundle keys to replace variables in when setting fires
		 * @see #hostSupportsOnFireVariableReplacement(Bundle)
		 * @see #setKeyEncoding(Bundle,String[],Encoding)
		*/	
		public static void setVariableReplaceKeys( Bundle resultBundleToHost, String [] listOfKeyNames ) {
			addStringArrayToBundleAsString( 
					listOfKeyNames, resultBundleToHost, BUNDLE_KEY_VARIABLE_REPLACE_STRINGS,
					"setVariableReplaceKeys"
			);
		}		

		/**
		 * Used by: plugin FireReceiver 
		 *
		 * Indicates to plugin whether the host will process variables which it passes back
		 *
		 * @param  extrasFromHost intent extras from the intent received by the FireReceiver
		 * @see #signalFinish(Context, Intent, int, Bundle)
		*/
		public static boolean hostSupportsVariableReturn( Bundle extrasFromHost ) {
			return hostSupports( extrasFromHost, EXTRA_HOST_CAPABILITY_SETTING_RETURN_VARIABLES );
		}
		
		 /**
         * Used by: plugin FireReceiver 
         *
         * Tell the host that the plugin has finished execution.
         *
         * This should only be used if RESULT_CODE_PENDING was returned by FireReceiver.onReceive().
         *
         * @param originalFireIntent the intent received from the host (via onReceive())
         * @param resultCode level of success in performing the settings
         * @param vars any variables that the plugin wants to set in the host
         * @see #hostSupportsSynchronousExecution(Bundle)
        */
        public static boolean signalFinish( Context context, Intent originalFireIntent, int resultCode, Bundle vars ) {
        
        	String errorPrefix = "signalFinish: ";
        
        	boolean okFlag = false;

        	String completionIntentString = (String) getExtraValueSafe( originalFireIntent, Setting.EXTRA_PLUGIN_COMPLETION_INTENT, String.class, "signalFinish" );

        	if ( completionIntentString != null ) {
        		
        		Uri completionIntentUri = null;
        		try {
        			completionIntentUri = Uri.parse( completionIntentString );
        		}
        		// 	should only throw NullPointer but don't particularly trust it
        		catch ( Exception e ) {
        			Log.w( TAG, errorPrefix + "couldn't parse " + completionIntentString );
        		}
        	
        		if ( completionIntentUri != null ) {
        			try {
        				Intent completionIntent = Intent.parseUri( completionIntentString, Intent.URI_INTENT_SCHEME );
            
        				completionIntent.putExtra( EXTRA_RESULT_CODE, resultCode );
                                                        
        				if ( vars != null )
        					completionIntent.putExtra( EXTRA_VARIABLES_BUNDLE, vars );
                                                        
        				context.sendBroadcast( completionIntent );
        			
        				okFlag = true;
        			}
        			catch ( URISyntaxException e ) {
        				Log.w( TAG, errorPrefix + "bad URI: " + completionIntentUri );
        			}
        		}
        	}
                
        	return okFlag;
        }

        /**
		 * Check for a hint on the timeout value the host is using.
		 * Used by: plugin FireReceiver.
		 * Requires Tasker 4.7+
		 *
		 * @param  extrasFromHost intent extras from the intent received by the FireReceiver	
		 * @return timeoutMS the hosts timeout setting for the action or -1 if no hint is available.
		 * 
		 * @see #REQUESTED_TIMEOUT_MS_NONE, REQUESTED_TIMEOUT_MS_MAX, REQUESTED_TIMEOUT_MS_NEVER
		 */
        public static int getHintTimeoutMS( Bundle extrasFromHost ) {

        	int timeoutMS = -1;
        	
			Bundle hintsBundle = (Bundle) TaskerPlugin.getBundleValueSafe( extrasFromHost, EXTRA_HINTS_BUNDLE, Bundle.class, "getHintTimeoutMS" );

			if ( hintsBundle != null ) {
				        	
				Integer val = (Integer) getBundleValueSafe( hintsBundle, BUNDLE_KEY_HINT_TIMEOUT_MS, Integer.class, "getHintTimeoutMS" );
				
				if ( val != null )
					timeoutMS = val;
			}
			
			return timeoutMS;
		}
	}
		
	// ----------------------------- CONDITION/EVENT PLUGIN ONLY --------------------------------- //
	
	public static class Condition {

		/**
		 * Used by: plugin QueryReceiver 
		 *
		 * Indicates to plugin whether the host will process variables which it passes back
		 *
		 * @param  extrasFromHost intent extras from the intent received by the QueryReceiver
		 * @see #addVariableBundle(Bundle, Bundle)
		*/
		public static boolean hostSupportsVariableReturn( Bundle extrasFromHost ) {
			return hostSupports( extrasFromHost,  EXTRA_HOST_CAPABILITY_CONDITION_RETURN_VARIABLES );
		}
	}

	// ----------------------------- EVENT PLUGIN ONLY --------------------------------- //
	
	public static class Event {
		
		public final static String	PASS_THROUGH_BUNDLE_MESSAGE_ID_KEY = BASE_KEY + ".MESSAGE_ID";
		
		private final static String	EXTRA_REQUEST_QUERY_PASS_THROUGH_DATA = EXTRAS_PREFIX + "PASS_THROUGH_DATA";
		
		/**
		 * @param  extrasFromHost intent extras from the intent received by the QueryReceiver
		 * @see #addPassThroughData(Intent, Bundle)
		*/
		public static boolean hostSupportsRequestQueryDataPassThrough( Bundle extrasFromHost ) {
			return hostSupports( extrasFromHost,  EXTRA_HOST_CAPABILITY_REQUEST_QUERY_DATA_PASS_THROUGH );
		}

		/**
		 * Specify a bundle of data (probably representing whatever change happened in the condition)
		 * which will be included in the QUERY_CONDITION broadcast sent by the host for each
		 * event instance of the plugin.
		 * 
		 * The minimal purpose is to enable the plugin to associate a QUERY_CONDITION to the
		 * with the REQUEST_QUERY that caused it.
		 * 
		 * Note that for security reasons it is advisable to also store a message ID with the bundle
		 * which can be compared to known IDs on receipt. The host cannot validate the source of
		 * REQUEST_QUERY intents so fake data may be passed. Replay attacks are also possible.
		 * addPassThroughMesssageID() can be used to add an ID if the plugin doesn't wish to add it's
		 * own ID to the pass through bundle.
		 * 
		 * Note also that there are several situations where REQUEST_QUERY will not result in a
		 * QUERY_CONDITION intent (e.g. event throttling by the host), so plugin-local data
		 * indexed with a message ID needs to be timestamped and eventually timed-out.
		 * 
		 * This function can be called multiple times, each time all keys in data will be added to
		 * that of previous calls.
		 * 
		 * @param requestQueryIntent intent being sent to the host
		 * @param data the data to be passed-through
		 * @see #hostSupportsRequestQueryDataPassThrough(Bundle)
		 * @see #retrievePassThroughData(Intent)
		 * @see #addPassThroughMessageID
		 * 
		*/
		public static void addPassThroughData( Intent requestQueryIntent, Bundle data ) {
			
			Bundle passThroughBundle = retrieveOrCreatePassThroughBundle( requestQueryIntent );
			
			passThroughBundle.putAll( data );
		}

		/**
		 * Retrieve the pass through data from a QUERY_REQUEST from the host which was generated
		 * by a REQUEST_QUERY from the plugin.
		 * 
		 * Note that if addPassThroughMessageID() was previously called, the data will contain an extra
		 * key TaskerPlugin.Event.PASS_THOUGH_BUNDLE_MESSAGE_ID_KEY.
		 * 
		 * @param queryConditionIntent QUERY_REQUEST sent from host
		 * @return data previously added to the REQUEST_QUERY intent
		 * @see #hostSupportsRequestQueryDataPassThrough(Bundle)
		 * @see #addPassThroughData(Intent,Bundle)
		*/
		public static Bundle retrievePassThroughData( Intent queryConditionIntent ) {
			return (Bundle) getExtraValueSafe( 
					queryConditionIntent, 
					EXTRA_REQUEST_QUERY_PASS_THROUGH_DATA, 
					Bundle.class, 
					"retrievePassThroughData" 
			);
		}
		
		/**
		 * Add a message ID to a REQUEST_QUERY intent which will then be included in the corresponding
		 * QUERY_CONDITION broadcast sent by the host for each event instance of the plugin.
		 * 
		 * The minimal purpose is to enable the plugin to associate a QUERY_CONDITION to the
		 * with the REQUEST_QUERY that caused it. It also allows the message to be verified
		 * by the plugin to prevent e.g. replay attacks
		 * 
		 * @param requestQueryIntent intent being sent to the host
		 * @return a guaranteed non-repeating within 100 calls message ID
		 * @see #hostSupportsRequestQueryDataPassThrough(Bundle)
		 * @see #retrievePassThroughData(Intent)
		 * @return an ID for the bundle so it can be identified and the caller verified when it is again received by the plugin
		 * 
		*/
		public static int addPassThroughMessageID( Intent requestQueryIntent ) {
			
			Bundle passThroughBundle = retrieveOrCreatePassThroughBundle( requestQueryIntent );
					
			int id = getPositiveNonRepeatingRandomInteger();
			
			passThroughBundle.putInt( PASS_THROUGH_BUNDLE_MESSAGE_ID_KEY, id );
			
			return id;
		}

		/*
		 * Retrieve the pass through data from a QUERY_REQUEST from the host which was generated
		 * by a REQUEST_QUERY from the plugin.
		 * 
		 * @param queryConditionIntent QUERY_REQUEST sent from host
		 * @return the ID which was passed through by the host, or -1 if no ID was found
		 * @see #hostSupportsRequestQueryDataPassThrough(Bundle)
		 * @see #addPassThroughData(Intent,Bundle)
		*/
		public static int retrievePassThroughMessageID( Intent queryConditionIntent ) {
	
			int toReturn = -1;
			
			Bundle passThroughData = Event.retrievePassThroughData( queryConditionIntent );
			
			if ( passThroughData != null ) {
				Integer id = (Integer) getBundleValueSafe( 
						passThroughData, 
						PASS_THROUGH_BUNDLE_MESSAGE_ID_KEY, 
						Integer.class,
						"retrievePassThroughMessageID"
				);
				
				if ( id != null )
					toReturn = id;
			}
			
			return toReturn;
		}
		
		// internal use
		private static Bundle retrieveOrCreatePassThroughBundle( Intent requestQueryIntent ) {

			Bundle passThroughBundle;
			
			if ( requestQueryIntent.hasExtra( EXTRA_REQUEST_QUERY_PASS_THROUGH_DATA ) )
				passThroughBundle = requestQueryIntent.getBundleExtra( EXTRA_REQUEST_QUERY_PASS_THROUGH_DATA );
			else {
				passThroughBundle = new Bundle();
				requestQueryIntent.putExtra( EXTRA_REQUEST_QUERY_PASS_THROUGH_DATA, passThroughBundle );
			}
			
			return passThroughBundle;
		}
	}
	// ---------------------------------- HOST  ----------------------------------------- //

	public static class Host {

		/**
		 * Tell the plugin what capabilities the host support. This should be called when sending
		 * intents to any EditActivity, FireReceiver or QueryReceiver.
		 *
		 * @param  toPlugin the intent we're sending
		 * @return capabilities one or more of the EXTRA_HOST_CAPABILITY_XXX flags 
		*/
		public static Intent addCapabilities( Intent toPlugin, int capabilities ) {
			return toPlugin.putExtra( EXTRA_HOST_CAPABILITIES, capabilities  );
		}

		/**
		* Add an intent to the fire intent before it goes to the plugin FireReceiver, which the plugin
		* can use to signal when it is finished. Only use if @code{pluginWantsSychronousExecution} is true.
		*	
		* @param fireIntent fire intent going to the plugin 
		* @param completionIntent intent which will signal the host that the plugin is finished.
		* Implementation is host-dependent.
       	*/
		public static void addCompletionIntent( Intent fireIntent, Intent completionIntent ) {
			fireIntent.putExtra( 
				Setting.EXTRA_PLUGIN_COMPLETION_INTENT, 
                        	completionIntent.toUri( Intent.URI_INTENT_SCHEME )
                	);
        }

		/**
	         * When a setting plugin is finished, it sends the host the intent which was passed to it
       		  * via @code{addCompletionIntent}. 
       		  *
       		  * @param completionIntent intent returned from the plugin when it finished.
       		  * @return resultCode measure of plugin success, defaults to UNKNOWN 
       	*/
		public static int getSettingResultCode( Intent completionIntent ) {
        
			Integer val = (Integer) getExtraValueSafe( completionIntent, Setting.EXTRA_RESULT_CODE, Integer.class, "getSettingResultCode" );

			return ( val == null ) ? Setting.RESULT_CODE_UNKNOWN : val;
		}

       	 /**
		 * Extract a bundle of variables from an intent received from the FireReceiver. This
		 * should be called if the host previously indicated to the plugin
		 * that it supports setting variable return.
		 *
		 * @param resultExtras getResultExtras() from BroadcastReceiver:onReceive()
		 * @return variables a bundle of variable name/value pairs
		 * @see #addCapabilities(Intent, int) 
		*/

		public static Bundle getVariablesBundle( Bundle resultExtras ) {
			return (Bundle) getBundleValueSafe( 
					resultExtras, EXTRA_VARIABLES_BUNDLE, Bundle.class, "getVariablesBundle" 
			);
		}

		/** 
		* Inform a setting plugin of the timeout value the host is using.
		* 
		* @param toPlugin the intent we're sending
		* @param timeoutMS the hosts timeout setting for the action. Note that this may differ from
		* that which the plugin requests.
		* @see #REQUESTED_TIMEOUT_MS_NONE, REQUESTED_TIMEOUT_MS_MAX, REQUESTED_TIMEOUT_MS_NEVER
		*/
		public static void addHintTimeoutMS( Intent toPlugin, int timeoutMS ) {
			getHintsBundle( toPlugin, "addHintTimeoutMS" ).putInt( BUNDLE_KEY_HINT_TIMEOUT_MS, timeoutMS );
		}

		private static Bundle getHintsBundle( Intent intent, String funcName ) {

			Bundle hintsBundle = (Bundle) getExtraValueSafe( intent, EXTRA_HINTS_BUNDLE, Bundle.class, funcName );
			
			if ( hintsBundle == null ) {
				hintsBundle = new Bundle();
				intent.putExtra( EXTRA_HINTS_BUNDLE, hintsBundle );
			}
			
			return hintsBundle;
		}
		
		public static boolean haveRequestedTimeout( Bundle extrasFromPluginEditActivity ) {
			return extrasFromPluginEditActivity.containsKey( Setting.EXTRA_REQUESTED_TIMEOUT );
		}
		
		public static int getRequestedTimeoutMS( Bundle extrasFromPluginEditActivity ) {
			return 
					(Integer) getBundleValueSafe( 
							extrasFromPluginEditActivity, Setting.EXTRA_REQUESTED_TIMEOUT,	Integer.class, "getRequestedTimeout" 
					)
			;
		}
		
		public static String [] getSettingVariableReplaceKeys( Bundle fromPluginEditActivity ) {
			return getStringArrayFromBundleString( 
					fromPluginEditActivity, Setting.BUNDLE_KEY_VARIABLE_REPLACE_STRINGS,
					"getSettingVariableReplaceKeys"
			);
		}
		
		public static String [] getKeysWithEncoding( Bundle fromPluginEditActivity, Encoding encoding ) {

			String [] toReturn = null;
			
			if ( Encoding.JSON.equals( encoding ) )
				toReturn = getStringArrayFromBundleString( 
						fromPluginEditActivity, TaskerPlugin.BUNDLE_KEY_ENCODING_JSON_KEYS,
						"getKeyEncoding:JSON"
				); 
			else
				Log.w( TAG, "Host.getKeyEncoding: unknown encoding " + encoding );
			
			return toReturn;
		}
		
		public static boolean haveRelevantVariables( Bundle b ) {
			return b.containsKey( BUNDLE_KEY_RELEVANT_VARIABLES );
		}
		
		public static void cleanRelevantVariables( Bundle b ) {
			b.remove( BUNDLE_KEY_RELEVANT_VARIABLES );
		}

		public static void cleanHints( Bundle extras ) {
			extras.remove( TaskerPlugin.EXTRA_HINTS_BUNDLE );
		}

		public static void cleanRequestedTimeout( Bundle extras ) {
			extras.remove( Setting.EXTRA_REQUESTED_TIMEOUT );
		}
		
		public static void cleanSettingReplaceVariables( Bundle b ) {
			b.remove( Setting.BUNDLE_KEY_VARIABLE_REPLACE_STRINGS );
		}
	}
	
	// ---------------------------------- HELPER FUNCTIONS -------------------------------- //

	private static Object getBundleValueSafe( Bundle b, String key, Class<?> expectedClass, String funcName ) {
		Object value = null;
		
		if ( b != null ) {
			if ( b.containsKey( key ) ) {
				Object obj = b.get( key );
				if ( obj == null )
					Log.w( TAG, funcName + ": " + key + ": null value" );
				else if ( obj.getClass() != expectedClass ) 
					Log.w( TAG, funcName + ": " + key + ": expected " + expectedClass.getClass().getName() + ", got " + obj.getClass().getName() );
				else
					value = obj;
			}
		}
		return value;
	}
	
	private static Object getExtraValueSafe( Intent i, String key, Class<?> expectedClass, String funcName ) {
		return ( i.hasExtra( key ) ) ?
                 getBundleValueSafe( i.getExtras(), key, expectedClass, funcName ) :
                 null;
	}

	private static boolean hostSupports( Bundle extrasFromHost, int capabilityFlag ) {
		Integer flags = (Integer) getBundleValueSafe( extrasFromHost, EXTRA_HOST_CAPABILITIES, Integer.class, "hostSupports" );
		return 
				( flags != null ) &&
				( ( flags & capabilityFlag ) > 0 )
			;
	}
	
    public static int getPackageVersionCode( PackageManager pm, String packageName ) {

    	int code = -1;
    	
    	if ( pm != null ) {
    		try {
    			PackageInfo pi = pm.getPackageInfo( packageName, 0 );
    			if ( pi != null ) 
    				code = pi.versionCode;
    		}
    		catch ( Exception e ) {
    			Log.e( TAG, "getPackageVersionCode: exception getting package info" );
    		}
    	}
    	
    	return code;
    }

    private static boolean variableNameIsLocal( String varName ) {

    	int digitCount = 0;
    	int length = varName.length();
    		
    	for ( int x = 0; x < length; x++ ) {
    		char ch = varName.charAt( x );
    			
    		if ( Character.isUpperCase( ch ) )
    			return false;
    		else if ( Character.isDigit( ch ) )
    			digitCount++;
    	}
    		
    	if ( digitCount == ( varName.length() - 1 ) )
    		return false;
    	
    	return true;
    }

    private static String [] getStringArrayFromBundleString( Bundle bundle, String key, String funcName ) {

		String spec = (String) getBundleValueSafe( bundle, key, String.class, funcName );

		String [] toReturn = null;
		
		if ( spec != null )
			toReturn = spec.split( " " );
		
		return toReturn;
	}

	private static void addStringArrayToBundleAsString( String [] toAdd, Bundle bundle, String key, String callerName ) {
		
		StringBuilder builder = new StringBuilder();
		
		if ( toAdd != null ) {
			
			for ( String keyName : toAdd ) {
			
				if ( keyName.contains( " " ) )
					Log.w( TAG, callerName + ": ignoring bad keyName containing space: " + keyName );
				else {
					if ( builder.length() > 0 )
						builder.append( ' ' );
					
					builder.append( keyName );
				}
				
				if ( builder.length() > 0 )
					bundle.putString( key, builder.toString() );
			}
		}
	}

    // state tracking for random number sequence
    private static int [] 		lastRandomsSeen = null;
	private static int 			randomInsertPointer = 0;
	private static SecureRandom sr = null;
	
	/**
	 * Generate a sequence of secure random positive integers which is guaranteed not to repeat
	 * in the last 100 calls to this function.
	 *
	 * @return a random positive integer
	*/
    public static int getPositiveNonRepeatingRandomInteger() {
		
    	// initialize on first call
		if ( sr == null ) {
			sr = new SecureRandom();
			lastRandomsSeen = new int[RANDOM_HISTORY_SIZE];
		
			for ( int x = 0; x < lastRandomsSeen.length; x++ )
				lastRandomsSeen[x] = -1;
		}

		int toReturn;
		do {
			// pick a number
			toReturn = sr.nextInt( Integer.MAX_VALUE );
	
			// check we havn't see it recently
			for ( int seen : lastRandomsSeen ) {
				if ( seen == toReturn ) {
					toReturn = -1;
					break;
				}
			}
		}
		while ( toReturn == -1 );
			
		// update history
		lastRandomsSeen[randomInsertPointer] = toReturn;
		randomInsertPointer = ( randomInsertPointer + 1 ) % lastRandomsSeen.length; 
		
		return toReturn;
    }

}