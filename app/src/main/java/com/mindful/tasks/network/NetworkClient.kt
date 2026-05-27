package com.mindful.tasks.network

import com.mindful.tasks.viewmodel.JournalEntry
import com.mindful.tasks.viewmodel.Priority
import com.mindful.tasks.viewmodel.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object NetworkClient {

    // 10.0.2.2 is the special IP Android uses to connect to localhost on your Mac
    private const val BASE_URL = "http://10.0.2.2:8080/api"

    // Helper function to send POST requests
    private suspend fun sendPostRequest(endpoint: String, jsonPayload: String): String? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/$endpoint")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            // Write JSON payload
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonPayload)
            writer.flush()
            writer.close()

            // Read response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                response.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    // Helper function to send GET requests
    private suspend fun sendGetRequest(endpoint: String, queryParams: String): String? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("$BASE_URL/$endpoint?$queryParams")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                response.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    // ----------------------------------------------------
    // API CALLS
    // ----------------------------------------------------

    suspend fun signup(username: String, password: String): Pair<Boolean, String> {
        val payload = JSONObject().apply {
            put("username", username)
            put("password", password)
        }.toString()

        val response = sendPostRequest("auth/signup", payload) ?: return Pair(false, "Network error")
        return try {
            val json = JSONObject(response)
            val success = json.getBoolean("success")
            val message = json.optString("message", "Success")
            Pair(success, message)
        } catch (e: Exception) {
            Pair(false, "Failed to parse response")
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val payload = JSONObject().apply {
            put("username", username)
            put("password", password)
        }.toString()

        val response = sendPostRequest("auth/login", payload) ?: return false
        return try {
            val json = JSONObject(response)
            json.getBoolean("success")
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTasks(username: String): List<Task> {
        val response = sendGetRequest("tasks", "username=$username") ?: return emptyList()
        return try {
            val jsonArray = JSONArray(response)
            val list = mutableListOf<Task>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Task(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        isCompleted = obj.getBoolean("isCompleted"),
                        priority = Priority.valueOf(obj.getString("priority")),
                        category = obj.getString("category")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveTasks(username: String, tasks: List<Task>): Boolean {
        val tasksArray = JSONArray().apply {
            tasks.forEach { task ->
                put(JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("isCompleted", task.isCompleted)
                    put("priority", task.priority.name)
                    put("category", task.category)
                })
            }
        }
        val payload = JSONObject().apply {
            put("username", username)
            put("tasks", tasksArray)
        }.toString()

        val response = sendPostRequest("tasks", payload) ?: return false
        return try {
            JSONObject(response).getBoolean("success")
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getJournal(username: String): List<JournalEntry> {
        val response = sendGetRequest("journal", "username=$username") ?: return emptyList()
        return try {
            val jsonArray = JSONArray(response)
            val list = mutableListOf<JournalEntry>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    JournalEntry(
                        id = obj.getString("id"),
                        text = obj.getString("text"),
                        date = obj.getString("date"),
                        moodEmoji = obj.getString("moodEmoji")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveJournal(username: String, journal: List<JournalEntry>): Boolean {
        val journalArray = JSONArray().apply {
            journal.forEach { entry ->
                put(JSONObject().apply {
                    put("id", entry.id)
                    put("text", entry.text)
                    put("date", entry.date)
                    put("moodEmoji", entry.moodEmoji)
                })
            }
        }
        val payload = JSONObject().apply {
            put("username", username)
            put("journal", journalArray)
        }.toString()

        val response = sendPostRequest("journal", payload) ?: return false
        return try {
            JSONObject(response).getBoolean("success")
        } catch (e: Exception) {
            false
        }
    }

    suspend fun queryAi(query: String, model: String): String {
        val payload = JSONObject().apply {
            put("query", query)
            put("model", model)
        }.toString()

        val response = sendPostRequest("ai/query", payload) ?: return "Unable to contact AI server. Is it running?"
        return try {
            JSONObject(response).getString("response")
        } catch (e: Exception) {
            "Error parsing AI response"
        }
    }
}
