/*
* Copyright (c) 2019 Razeware LLC
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
* distribute, sublicense, create a derivative work, and/or sell copies of the
* Software in any work that is designed, intended, or marketed for pedagogical or
* instructional purposes related to programming, coding, application development,
* or information technology.  Permission for such use, copying, modification,
* merger, publication, distribution, sublicensing, creation of derivative works,
* or sale is expressly withheld.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package com.raywenderlich.listmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ListSelectionRecyclerViewAdapter.ListSelectionRecyclerViewClickListener {

  //This is part 1
  val listDataManager: ListDataManager = ListDataManager(this)

  lateinit var listsRecyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)


    
    fab.setOnClickListener {
      showCreateListDialog()
    }

    // 1
    val lists = listDataManager.readLists()
    listsRecyclerView = findViewById(R.id.lists_recyclerview)
    listsRecyclerView.layoutManager = LinearLayoutManager(this)

    // 2
    listsRecyclerView.adapter = ListSelectionRecyclerViewAdapter(lists, this)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun showCreateListDialog() {
    // 1
    val dialogTitle = getString(R.string.name_of_list)
    val positiveButtonTitle = getString(R.string.create_list)

    // 2
    val builder = AlertDialog.Builder(this)
    val listTitleEditText = EditText(this)
    listTitleEditText.inputType = InputType.TYPE_CLASS_TEXT

    builder.setTitle(dialogTitle)
    builder.setView(listTitleEditText)

    builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->

      val list = TaskList(listTitleEditText.text.toString())
      listDataManager.saveList(list)

      val recyclerAdapter = listsRecyclerView.adapter as ListSelectionRecyclerViewAdapter
      recyclerAdapter.addList(list)

      dialog.dismiss()
      showListDetail(list)
    }

    builder.create().show()
  }

  private fun showListDetail(list: TaskList) {
    val listDetailIntent = Intent(this, ListDetailActivity::class.java)
    listDetailIntent.putExtra(INTENT_LIST_KEY, list)

    startActivityForResult(listDetailIntent, LIST_DETAIL_REQUEST_CODE)
  }

  override fun listItemClicked(list: TaskList) {
    showListDetail(list)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data:
  Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    // 1
    if (requestCode == LIST_DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      // 2
      data?.let {
        // 3
        listDataManager.saveList(data.getParcelableExtra(INTENT_LIST_KEY))
        updateLists()
      }
    }
  }

  private fun updateLists() {
    val lists = listDataManager.readLists()
    listsRecyclerView.adapter = ListSelectionRecyclerViewAdapter(lists, this)
  }

  companion object {
    const val INTENT_LIST_KEY = "list"
    const val LIST_DETAIL_REQUEST_CODE = 123
  }
}
