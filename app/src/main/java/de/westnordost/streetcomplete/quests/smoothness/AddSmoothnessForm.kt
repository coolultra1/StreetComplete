package de.westnordost.streetcomplete.quests.smoothness

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.osm.mapdata.Way
import de.westnordost.streetcomplete.ktx.isArea
import de.westnordost.streetcomplete.quests.AImageListQuestAnswerFragment
import de.westnordost.streetcomplete.quests.AnswerItem
import de.westnordost.streetcomplete.quests.surface.Surface
import de.westnordost.streetcomplete.quests.surface.asItem
import de.westnordost.streetcomplete.view.image_select.ItemViewHolder

class AddSmoothnessForm : AImageListQuestAnswerFragment<Smoothness, SmoothnessAnswer>() {

    override val descriptionResId = R.string.quest_smoothness_hint

    override val otherAnswers get() = listOfNotNull(
        AnswerItem(R.string.quest_smoothness_wrong_surface) { surfaceWrong() },
        createConvertToStepsAnswer(),
        AnswerItem(R.string.quest_smoothness_obstacle) { showObstacleHint() }
    )

    private val surfaceTag get() = osmElement!!.tags["surface"]

    override val items get() = Smoothness.values().toItems(requireContext(), surfaceTag!!)

    override val itemsPerRow = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageSelector.cellLayoutId = R.layout.cell_labeled_icon_select_smoothness
    }

    override val moveFavoritesToFront = false

    override fun onClickOk(selectedItems: List<Smoothness>) {
        applyAnswer(SmoothnessValueAnswer(selectedItems.single()))
    }

    private fun showObstacleHint() {
        activity?.let { AlertDialog.Builder(it)
            .setMessage(R.string.quest_smoothness_obstacle_hint)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        }
    }

    private fun surfaceWrong() {
        val surfaceType = Surface.values().find { it.osmValue == surfaceTag }!!
        showWrongSurfaceDialog(surfaceType)
    }

    private fun showWrongSurfaceDialog(surface: Surface) {
        val inflater = LayoutInflater.from(requireContext())
        val inner = inflater.inflate(R.layout.dialog_quest_smoothness_wrong_surface, null, false)
        ItemViewHolder(inner.findViewById(R.id.item_view)).bind(surface.asItem())

        AlertDialog.Builder(requireContext())
            .setView(inner)
            .setPositiveButton(R.string.quest_generic_hasFeature_yes_leave_note) { _, _ -> composeNote() }
            .setNegativeButton(R.string.quest_generic_hasFeature_no) { _, _ -> applyAnswer(WrongSurfaceAnswer) }
            .show()
    }

    private fun createConvertToStepsAnswer(): AnswerItem? {
        val way = osmElement as? Way ?: return null
        if (way.isArea()) return null

        // only in AddPathSmoothness quest
        if (!ALL_PATHS_EXCEPT_STEPS.contains(way.tags["highway"])) return null

        return AnswerItem(R.string.quest_generic_answer_is_actually_steps) {
            applyAnswer(IsActuallyStepsAnswer)
        }
    }
}
