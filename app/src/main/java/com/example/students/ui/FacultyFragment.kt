package com.example.students.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.students.R
import com.example.students.data.Faculty
import com.example.students.databinding.FragmentFacultyBinding
import com.example.students.models.FacultyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val FACULTY_TAG = "FacultyFragment"
const  val FACULTY_TITLE="Университет"

class FacultyFragment : Fragment() { //является подклассом  Fragment
    private lateinit var viewModel: FacultyViewModel //связь с фрагментом
    private var _binding: FragmentFacultyBinding? = null
    val binding
        get() = _binding!!
    //экземпляром класса `FacultyListAdapter`, который инициализируется пустым списком.
    private var adapter: FacultyListAdapter = FacultyListAdapter(emptyList())
    //возвращает новый экземпляр класса `FacultyFragment`.
    companion object {
        fun newInstance() = FacultyFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentFacultyBinding.inflate(inflater, container,false)
        //отображение по вертикали
        binding.rvFaculty.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FacultyViewModel:: class.java)
        viewModel.university.observe(viewLifecycleOwner){
            adapter=FacultyListAdapter(it)
            binding.rvFaculty.adapter=adapter
        }
        callbacks?.setTitle(FACULTY_TITLE) //безопасный вызов метода
        viewModel.loadFaculty()
    }

    private var lastItemView: View? = null

    private inner class FacultyHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
        lateinit var faculty: Faculty

        fun bind(faculty: Faculty){
            this.faculty=faculty
            itemView.findViewById<TextView>(R.id.tvFacultyElement).text=faculty.name
            itemView.findViewById<ConstraintLayout>(R.id.crudButtons).visibility = View.GONE
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?){
            callbacks?.showFaculty (faculty.id!!)
        }

        override fun onLongClick(v: View?): Boolean {
            // Обработка долгого нажатия
            val cl = itemView.findViewById<ConstraintLayout>(R.id.crudButtons)

            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.crudButtons)?.visibility = View.GONE
            lastItemView = if (lastItemView == itemView) null else itemView
            if (cl.visibility == View.VISIBLE) {
                itemView.findViewById<ImageButton>(R.id.delBtn).setOnClickListener {
                    commitDeleteDialog(faculty)
                }
                itemView.findViewById<ImageButton>(R.id.editBtn).setOnClickListener {
                    showEditDialog(faculty)
                }
            }
            return true // Возвращаем true, чтобы сигнализировать, что событие было обработано
        }
    }

    private fun showEditDialog(faculty: Faculty){//создание диалогового окна
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.name_input, null)//подсоединение html
        builder.setView(dialogView)
        val nameInput = dialogView.findViewById(R.id.editTextTextPersonName) as EditText//элементы управления в макете
        val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView//элементы управления в макете
        builder.setTitle("Редактирование")//устанавливаемм заголовок
        nameInput.setText(faculty.name)

        tvInfo.text =getString(R.string.inputFaculty)
        builder.setPositiveButton(getString(R.string.commit)){_, _ ->//обработчик нажатия ок
            val s = nameInput.text.toString()//получение значения из поля
            if (s.isNotBlank()){
                CoroutineScope(Dispatchers.Main). launch {
                    viewModel.editFaculty(s, faculty)
                }//вызов метода нф в репозитории
            }
        }
        builder.setNegativeButton(R.string.cancel, null)
        val alert = builder.create()
        alert.show()
    }

    private fun commitDeleteDialog(faculty: Faculty) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить факульт ${faculty.name} из списка?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            CoroutineScope(Dispatchers.Main). launch {
                viewModel.deleteFaculty(faculty)
            }
            Toast.makeText(requireContext(), "Факультет успешно удалён.", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private inner class FacultyListAdapter(private val items: List<Faculty>) : RecyclerView.Adapter<FacultyHolder>(){
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FacultyHolder {
            val view = layoutInflater.inflate(R.layout.element_faculty_list,parent,false)
            return FacultyHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: FacultyHolder, position: Int) {
            holder.bind(items[position])
        }
    }
    //интерфейс для изменения title приложения на университет . Взаимодействие активити и фрагмента
    interface Callbacks{
        fun setTitle(_title: String)
        fun showFaculty (id: Long)
    }
    var callbacks : Callbacks? =null

    override fun onAttach(context: Context){
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onDetach(){
        callbacks=null
        super.onDetach()
    }
    //
}